package team.ictdb.mostdb.test;

import org.junit.Test;

import team.ictdb.mostdb.MostDB;

public class MostDBTest {

  public MostDBTest() {
    // TODO Auto-generated constructor stub
  }

  @Test
  public void check() throws Exception {
    MostDB.instance().connect();
    MostDB.instance().shutdown();
  }

}
