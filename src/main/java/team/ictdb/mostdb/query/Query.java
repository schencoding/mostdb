package team.ictdb.mostdb.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects; 

public class Query {
  
  public enum AggregateFunc {
    COUNT, SUM, AVG, MIN, MAX;
  }

  public enum ResultStyle {
    DEFAULT, E, EB, EBS;
  }
  
  public static final long NO_WINDOW = -1;
  
  public ResultStyle style = ResultStyle.DEFAULT;
  
  public String fromTable = null;
  public TimeCondition timeCondition = null;
  public Map<String, Collection<String>> tagConditions;
  public Collection<String> groupbyTags;
  public long timeWindowSize = NO_WINDOW;
  
  public Collection<ValueCondition> valueConditions;
  public Collection<String> selectTags;
  public Collection<String> selectMeass;
  public Collection<Aggregate> aggregates;

  public Query() {
    tagConditions = new HashMap<String, Collection<String>>();
    groupbyTags = new HashSet<String>();
    valueConditions = new HashSet<ValueCondition>();
    selectTags = new HashSet<String>();
    selectMeass = new HashSet<String>();
    aggregates = new HashSet<Aggregate>();
  }
  
  public TransformedQuery transform() {
    return new TransformedQuery(this);
  }
  
  public static class TimeCondition {
    public long lowerBound = Long.MIN_VALUE; // included
    public long upperBound = Long.MAX_VALUE; // included
    // point query: lowerBound == upperBound
  }
  
  public static class ValueCondition {
    public String meas;
    public long lowerBound = Long.MIN_VALUE; // included
    public long upperBound = Long.MAX_VALUE; // included
    // point query: lowerBound == upperBound
    
    public ValueCondition(String meas, long lb, long ub) {
      this.meas = meas;
      this.lowerBound = lb;
      this.upperBound = ub;
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ValueCondition other = (ValueCondition)o;
      return meas.equals(other.meas) && lowerBound == other.lowerBound 
          && upperBound == other.upperBound;
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(meas, lowerBound, upperBound);
    }
  }
  
  public static class Aggregate {
    public String meas;
    public AggregateFunc func;
    
    public Aggregate(String meas, AggregateFunc func) {
      this.meas = meas;
      this.func = func;
    }
    
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Aggregate other = (Aggregate)o;
      return meas.equals(other.meas) && func == other.func;
    }
    
    @Override
    public int hashCode() {
      return Objects.hash(meas, func);
    }
  }

}
