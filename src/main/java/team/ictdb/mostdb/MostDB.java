package team.ictdb.mostdb;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

import team.ictdb.mostdb.insert.Buffer;
import team.ictdb.mostdb.query.Query;
import team.ictdb.mostdb.query.TransformedQuery;
import team.ictdb.mostdb.schema.Schema;

public class MostDB {
  
  private static MostDB instance = null;
  private static final Logger LOGGER = LogManager.getLogger();
  
  private InfluxDBClient influxDB;
  private String url = "http://127.0.0.1:8086/";
  private String org = "mostdb";
  private String bucket = "mostdb";
  private char[] token = ("O3alk2txpszzqW4i7n51Dy3KBCLdx6XpRdd1DKJFG-"
      + "9NWy9XXWktH7hwjyMfP6dEgWPcMtBUtySyeiLhKYOUDA==").toCharArray();
  
  static {
    String ext = null;
    String osName = System.getProperty("os.name");
    if (osName.toLowerCase().startsWith("win")) {
      ext = "dll";
    } else if (osName.toLowerCase().startsWith("mac")) {
      ext = "dylib";
    } else {
      ext = "so";
    }
    System.load("/usr/local/lib/libmostdb_core."+ext); // Maybe some other path
    LOGGER.info("libmostdb_core."+ext+" loaded.");
  }
  
  public static class Row {
    public long timestamp;
    public String[] tags; // tagCount in Meta
    public double[] values; // valueCount in Meta
  }
  
  public static MostDB instance() { 
    return instance==null ? instance=new MostDB() : instance; 
  }
  
  private MostDB() {}
  
  public InfluxDBClient getInfluxDB() { return influxDB; }
  
  public void connect() throws IOException {
    if (influxDB != null) {
      throw new IOException("Connected");
    }
    influxDB = InfluxDBClientFactory.create(url, token, org, bucket);
    if (influxDB.ping() == null) {
      throw new IOException("Fail to connect InfluxDB.");
    }
    if (influxDB.getBucketsApi().findBucketByName(bucket) == null) {
      throw new IOException("Bucket \'" + bucket + "\' not created");
    }
    LOGGER.info("InfluxDB connected.");

    Schema.loadMeta();
    Buffer.prepare();
  }
  
  public void shutdown() throws Exception {
    Buffer.shutdown();
    Schema.storeMeta();
    if (influxDB.ping() == null) {
      return;
    }
    influxDB.close();
    influxDB = null;
    LOGGER.info("InfluxDB closed.");
  }
  
  // Attention: Schema not persistent until shutdown
  public synchronized void createTable(String tableName, Schema schema) {
    if (Schema.tables.containsKey(tableName)) {
      LOGGER.info("Table \'" + tableName + "\' already exists.");
      return;
    }
    Schema.tables.put(tableName, schema);
    LOGGER.info("Table \'" + tableName + "\' created.");
  }
  
  private ThreadLocal<StringBuilder> seriesIDBuilder = 
      ThreadLocal.withInitial(() -> new StringBuilder());
  
  public void insert(String tableName, Row row) throws Exception {
    Schema schema = Schema.tables.get(tableName);
    if (schema == null) {
      throw new IOException("No such table: " + tableName);
    }
    StringBuilder builder = seriesIDBuilder.get();
    builder.setLength(0);
    builder.append(tableName);
    for (String tag : row.tags) {
      builder.append(",").append(tag);
    }
    String seriesGroupID = builder.toString();
    Buffer[] buffers;
    synchronized (Buffer.buffersMap) {
      buffers = Buffer.buffersMap.get(seriesGroupID);
      if (buffers == null) {
        buffers = new Buffer[schema.getMeasNames().length];
        for (int i = 0; i < buffers.length; i ++) {
          buffers[i] = new Buffer(tableName, row.tags, schema.getMeasNames()[i]);
        }
        Buffer.buffersMap.put(seriesGroupID, buffers);
      }
    }
    insert(buffers, row);
  }
  
  public void insert(Buffer[] buffers, Row row) throws Exception {
    for (int i = 0; i < row.values.length; i ++) {
      buffers[i].input(row.timestamp, row.values[i]);
    }
  }
  
  public void query(Query query) throws Exception {
    TransformedQuery tq = query.transform();
    tq.execute();
  }
  
}
