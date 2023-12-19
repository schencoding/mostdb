package team.ictdb.mostdb.query;

import java.util.Collection;
import java.util.Map;

enum AggregateFunc{
  COUNT, SUM, AVG, MIN, MAX;
}

public class Query {
  
  public String fromTable;
  public TimeCondition timeCondition;
  public Map<String, Collection<String>> tagConditions;
  public Collection<String> groupbyTags;
  public long timeWindowSize;
  
  public Collection<ValueCondition> valueConditions;
  public Collection<String> selectTags;
  public Collection<String> selectMeass;
  public Collection<Aggregate> aggregates; // valid only when selectMeass==null

  public Query() {
    /* empty */
  }
  
  public void Transform() {
    
    // TODO
  }
  
  public class TimeCondition {
    public long lowerBound = Long.MIN_VALUE; // included
    public long upperBound = Long.MAX_VALUE; // included
    // point query: lowerBound == upperBound
  }
  
  public class ValueCondition {
    public String meas;
    public long lowerBound = Long.MIN_VALUE; // included
    public long upperBound = Long.MAX_VALUE; // included
    // point query: lowerBound == upperBound
  }
  
  public class Aggregate {
    String meas;
    AggregateFunc func;
  }

}
