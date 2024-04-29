package team.ictdb.mostdb.test;

//import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;

import team.ictdb.mostdb.MostDB;
import team.ictdb.mostdb.query.Query;
import team.ictdb.mostdb.query.Query.Aggregate;
import team.ictdb.mostdb.query.Query.AggregateFunc;
import team.ictdb.mostdb.query.Query.ValueCondition;
import team.ictdb.mostdb.schema.Schema;

public class TransformQueryTest {

  public TransformQueryTest() {
    // TODO Auto-generated constructor stub
  }

  @Test
  public void transformQuery() throws Exception {
    MostDB.instance().connect();
    Schema schema = new Schema(
        new String[]{"tagA", "tagB", "tagC"}, 
        new String[]{"valA", "valB", "valC", "valD"}, 
        new float[]{0.01f, 0.02f, 0.01f, 0.1f},
        true, false
        );

    MostDB.instance().createTable("querytable", schema);
    MostDB.instance().shutdown();
    
    MostDB.instance().connect();
    Query q1 = new Query();
    q1.fromTable = "querytable";
    Collection<String> tagValues = new HashSet<String>();
    tagValues.add("tagA1");
    tagValues.add("tagA2");
    q1.tagConditions.put("tagA", tagValues);
    q1.groupbyTags.add("tagB");
    q1.valueConditions.add(new ValueCondition("valA", 0, 100));
    q1.valueConditions.add(new ValueCondition("valB", 0, 100));
    q1.selectTags.add("tagB");
    q1.aggregates.add(new Aggregate("valB", AggregateFunc.AVG));
    q1.aggregates.add(new Aggregate("valC", AggregateFunc.SUM));
    //q1.transform();
    MostDB.instance().query(q1); // TODO: comment it!
    MostDB.instance().shutdown();
  }
}

