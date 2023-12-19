package team.ictdb.mostdb.schema;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Schema {
  
  private static final Logger LOGGER = LogManager.getLogger();
  public static Map<String, Schema> tables = new HashMap<>();
  private static String dirPath = System.getProperty("user.home") + "/.MostDB";
  
  static {
    File dir = new File(dirPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
  }
  
  private final String[] tagNames;
  private final String[] measNames;
  private final float[] errorBounds;
  private final boolean timestampRegular;
  private final boolean absoluteError;
  
  public long globalStart = -1;
  public int gap = 1; // used in regular-timestamp case
  
  // not stored
  private final Map<String, Integer> tagName2Id = new HashMap<>();
  private final Map<String, Integer> measName2Id = new HashMap<>();

  public Schema(String[] tagNames, String[] measNames, float[] errorBounds,
      boolean regular, boolean absoluteError) {
    this.tagNames = tagNames;
    this.measNames = measNames;
    this.errorBounds = errorBounds;
    this.timestampRegular = regular;
    this.absoluteError = absoluteError;
    for (int i = 0; i < this.tagNames.length; i ++) {
      tagName2Id.put(this.tagNames[i], i);
    }
    for (int i = 0; i < this.measNames.length; i ++) {
      measName2Id.put(this.measNames[i], i);
    }
  }

  public String[] getTagNames() { return tagNames; }
  public String[] getMeasNames() { return measNames; }
  public float[] getErrorBound() { return errorBounds; }
  public boolean isRegularTimestamp() { return timestampRegular; }
  public boolean isAbsoluteError() { return absoluteError; }
  public int getTagId(String name) { return tagName2Id.get(name); }
  public int getMeasId(String name) { return measName2Id.get(name); }
  
  public static void loadMeta() throws IOException {
    LOGGER.info("Loading meta data...");
    File file = new File(dirPath, "schema.txt");
    if (!file.exists()) {
      LOGGER.info("No existing schema file.");
      return;
    }
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line=reader.readLine()) != null && !line.isEmpty()) {
      String[] parts = line.split(",");
      int index = 0;
      String tableName = parts[index++];
      String[] tagNames = new String[Integer.parseInt(parts[index++])];
      for (int i = 0; i < tagNames.length; i ++) {
        tagNames[i] = parts[index++];
      }
      String[] measNames = new String[Integer.parseInt(parts[index++])];
      for (int i = 0; i < measNames.length; i ++) {
        measNames[i] = parts[index++];
      }
      float[] errorBounds = new float[measNames.length];
      for (int i = 0; i < measNames.length; i ++) {
        errorBounds[i] = Float.parseFloat(parts[index++]);
      }
      boolean timestampRegular = Boolean.parseBoolean(parts[index++]);
      boolean absoluteError = Boolean.parseBoolean(parts[index++]);
      Schema schema = new Schema(tagNames, measNames, errorBounds, 
          timestampRegular, absoluteError);
      schema.globalStart = Long.parseLong(parts[index++]);
      schema.gap = Integer.parseInt(parts[index++]);
      tables.put(tableName, schema);
    }
    reader.close();
    LOGGER.info("Meta data loaded.");
  }
  
  public static void storeMeta() throws IOException {
    LOGGER.info("Storing meta data...");
    File file = new File(dirPath, "schema.txt");
    file.delete();
    file.createNewFile();
    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
    for (Map.Entry<String, Schema> entry : tables.entrySet()) {
      writer.write(entry.getKey());
      writer.write(",");
      Schema schema = entry.getValue();
      writer.write(Integer.toString(schema.tagNames.length));
      writer.write(",");
      for (String tagName : schema.tagNames) {
        writer.write(tagName);
        writer.write(",");
      }
      writer.write(Integer.toString(schema.measNames.length));
      writer.write(",");
      for (String measName : schema.measNames) {
        writer.write(measName);
        writer.write(",");
      }
      for (float errorBound : schema.errorBounds) {
        writer.write(Float.toString(errorBound));
        writer.write(",");
      }
      writer.write(Boolean.toString(schema.timestampRegular));
      writer.write(",");
      writer.write(Boolean.toString(schema.absoluteError));
      writer.write(",");
      writer.write(Long.toString(schema.globalStart));
      writer.write(",");
      writer.write(Integer.toString(schema.gap));
      writer.newLine();
    }
    writer.close();
    LOGGER.info("Meta data stored.");
  }
  
}
