package team.ictdb.mostdb.query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import team.ictdb.mostdb.query.Query.Aggregate;
import team.ictdb.mostdb.query.Query.ResultStyle;
import team.ictdb.mostdb.query.Query.ValueCondition;
import team.ictdb.mostdb.schema.Schema;

public class TransformedQuery {
  
  static interface OperatorWrapper {
    public OperatorWrapper getChild();
    public void setChild(OperatorWrapper op);
    public long getNativeOperator();
    public void buildOperatorTree();
    public void buildOperator();
  }
  
  static abstract class AbstractOpWrapper implements OperatorWrapper {
    ResultStyle style = ResultStyle.DEFAULT;
    OperatorWrapper child = null;
    long nativePtr = 0;
    
    @Override public OperatorWrapper getChild() { return child; }
    @Override public void setChild(OperatorWrapper op) { child = op; }
    @Override public long getNativeOperator() { return nativePtr; }
    public AbstractOpWrapper(ResultStyle style) { this.style = style; }
    
    @Override
    public void buildOperatorTree() {
      if (child != null) child.buildOperatorTree();
      buildOperator();
    }
  }
  
  private ResultStyle style = ResultStyle.DEFAULT;
  private OperatorWrapper top;
  private Collection<WeavingScan> scans = new ArrayList<>();

  public TransformedQuery(Query q) {
    validate(q);
    style = q.style;
    
    Collection<String> involvedMeass = new HashSet<String>(q.selectMeass);
    for (ValueCondition vc : q.valueConditions) {
      involvedMeass.add(vc.meas);
    }
    for (Aggregate aggr : q.aggregates) {
      involvedMeass.add(aggr.meas);
    }
    WeavingScan scan = new WeavingScan(style, q.fromTable, q.timeCondition, 
        q.tagConditions, q.groupbyTags, q.timeWindowSize, q.selectTags, 
        involvedMeass);
    scan.setChild(null);
    scans.add(scan);
    top = scan;
    
    if (!q.valueConditions.isEmpty()) {
      DualModeFilter filter = new DualModeFilter(style); // TODO
      filter.setChild(top);
      top = filter;
    }
    
    if (!q.aggregates.isEmpty()) {
      DualModeAggregate aggr = new DualModeAggregate(style); // TODO
      aggr.setChild(top);
      top = aggr;
    }
    
    DualModeOutput output = new DualModeOutput(style);
    output.setChild(top);
    top = output;
    
    top.buildOperatorTree();
  }
  
  public OperatorWrapper getTopOperator() {
    return top;
  }
  
  public void execute() {
    for (WeavingScan scan : scans) {
      scan.scan();
    }
    // TODO
  }
  
  private void validate(Query q) {
    assert(q.fromTable != null) : "No source table in query";
    Schema schema = Schema.tables.get(q.fromTable);
    assert(schema != null) : "Bad table name";
    
    for (String tagName : q.tagConditions.keySet()) {
      assert(schema.isTagNameExists(tagName));
    }
    for (String tagName : q.groupbyTags) {
      assert(schema.isTagNameExists(tagName));
    }
    for (ValueCondition vc : q.valueConditions) {
      assert(schema.isMeasNameExists(vc.meas));
    }
    for (String tagName : q.selectTags) {
      assert(schema.isTagNameExists(tagName));
    }
    for (String measName : q.selectMeass) {
      assert(schema.isMeasNameExists(measName));
    }
    for (Aggregate aggr : q.aggregates) {
      assert(schema.isMeasNameExists(aggr.meas));
    }
    if (q.aggregates.isEmpty()) {
      assert(q.groupbyTags.isEmpty());
      assert(q.timeWindowSize == Query.NO_WINDOW);
    } else {
      assert(q.selectMeass.isEmpty());
      assert(q.groupbyTags.containsAll(q.selectTags));
    }
  }

}
