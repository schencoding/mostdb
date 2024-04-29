package team.ictdb.mostdb.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import team.ictdb.mostdb.MostDB;
import team.ictdb.mostdb.MostDB.Row;
import team.ictdb.mostdb.insert.Buffer;
import team.ictdb.mostdb.schema.Schema;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InsertTest {

  public InsertTest() {
    // TODO Auto-generated constructor stub
  }
  
  @Test
  public void aPlainInsert() throws Exception {
    MostDB.instance().connect();
    Schema schema = new Schema(
        new String[]{"tag1", "tag2"}, 
        new String[]{"val1", "val2"}, 
        new float[]{0.01f, 0.01f},
        true, false
        );
    MostDB.instance().createTable("naivetable", schema);
    Schema.tables.get("naivetable").globalStart = 10000;
    
    String[][] tagCombinations = new String[][] {
      new String[] {"tv11", "tv21"}, new String[] {"tv11", "tv22"},
      new String[] {"tv12", "tv21"}, new String[] {"tv12", "tv22"},
    };
    for (int i = 0; i < 3 * Buffer.MEMTABLE * Buffer.CAPACITY - 5; i ++) {
      for (int j = 0; j < tagCombinations.length; j ++) {
        Row row = new Row();
        row.timestamp = 10000 + i;
        row.tags = tagCombinations[j];
        row.values = new double[]{0.01*i, 0.02*i};
        MostDB.instance().insert("naivetable", row);
      }
    }
    MostDB.instance().shutdown();
  }
  
  @Test
  public void bulkLoad() throws Exception {
    MostDB.instance().connect();
    Schema schema = new Schema(
        new String[]{"tag1", "tag2"}, 
        new String[]{"val1", "val2"}, 
        new float[]{0.01f, 0.01f},
        true, false
        );
    MostDB.instance().createTable("bulktable", schema);
    Schema.tables.get("bulktable").globalStart = 10000;
    
    String[][] tagCombinations = new String[][] {
      new String[] {"tv11", "tv21"}, new String[] {"tv11", "tv22"},
      new String[] {"tv12", "tv21"}, new String[] {"tv12", "tv22"},
    };
    Buffer[][] bufferss = new Buffer[tagCombinations.length][];
    for (int i = 0; i < bufferss.length; i ++) {
      bufferss[i] = new Buffer[schema.getMeasNames().length];
      for (int j = 0; j < bufferss[i].length; j ++) {
        bufferss[i][j] = new Buffer("bulktable", tagCombinations[i], 
            schema.getMeasNames()[j]);
      }
    }
    Row row = new Row();
    row.values = new double[schema.getMeasNames().length];
    for (int i = 0; i < 64 * Buffer.MEMTABLE * Buffer.CAPACITY - 5; i ++) {
      for (int j = 0; j < bufferss.length; j ++) {
        row.timestamp = 10000 + i;
        //row.tags = tagCombinations[j];
        row.values[0] = 0.01*i;
        row.values[1] = 0.02*i;
        MostDB.instance().insert(bufferss[j], row);
      }
    }
    MostDB.instance().shutdown();
  }
}
