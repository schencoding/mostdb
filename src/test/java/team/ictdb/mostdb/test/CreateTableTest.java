package team.ictdb.mostdb.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import team.ictdb.mostdb.MostDB;
import team.ictdb.mostdb.schema.Schema;

public class CreateTableTest {

  public CreateTableTest() {
    // TODO Auto-generated constructor stub
  }

  @Test
  public void createTable() throws Exception {
    MostDB.instance().connect();
    
    Schema schema1 = new Schema(
        new String[]{"tag11", "tag12"}, 
        new String[]{"val11", "val12"}, 
        new float[]{0.01f, 0.02f},
        true, false
        );
    Schema schema2 = new Schema(
        new String[]{"tag21", "tag22", "tag23"}, 
        new String[]{"val21", "val22", "val23"}, 
        new float[]{0.10f, 0.10f, 0.10f},
        true, true
        );
    MostDB.instance().createTable("naivetable1", schema1);
    MostDB.instance().createTable("naivetable2", schema2);
    
    MostDB.instance().shutdown();
    
    MostDB.instance().connect();
    Schema s1 = Schema.tables.get("naivetable1");
    Schema s2 = Schema.tables.get("naivetable2");
    assertEquals(s1.getTagNames().length, 2);
    assertEquals(s1.getMeasNames()[1], "val12");
    assertEquals(s2.isRegularTimestamp(), true);
    MostDB.instance().shutdown();
  }
}
