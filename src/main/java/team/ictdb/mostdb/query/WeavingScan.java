package team.ictdb.mostdb.query;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import team.ictdb.mostdb.query.Query.ResultStyle;
import team.ictdb.mostdb.query.Query.TimeCondition;

public class WeavingScan extends TransformedQuery.AbstractOpWrapper {
  
  private static final Logger LOGGER = LogManager.getLogger();
  
  private String fromTable;
  private TimeCondition timeCondition;
  private Map<String, Collection<String>> tagConditions;
  private Collection<String> groupbyTags;
  private long timeWindowSize;
  private Collection<String> selectTags;
  private Collection<String> involvedMeass;

  public WeavingScan(ResultStyle style, String fromTable, TimeCondition tc, 
      Map<String, Collection<String>> tagConditions, 
      Collection<String> groupbyTags, long timeWindowSize, 
      Collection<String> selectTags, Collection<String> involvedMeass) {
    super(style);
    this.fromTable = fromTable;
    this.timeCondition = tc;
    this.tagConditions = new HashMap<>();
    for (Entry<String, Collection<String>> entry : tagConditions.entrySet()) {
      this.tagConditions.put(entry.getKey(), new HashSet<>(entry.getValue()));
    }
    this.groupbyTags = new HashSet<>(groupbyTags);
    this.timeWindowSize = timeWindowSize;
    this.selectTags = new HashSet<>(selectTags);
    this.involvedMeass = new HashSet<>(involvedMeass);
  } 

  private boolean scanned = false;
  public void scan() {
    if (scanned) return;
    LOGGER.info("Scanning table: ", fromTable);
    // TODO
    scanned = true;
  }
  
  @Override
  public void buildOperator() {
    LOGGER.info("Build Weaving Scan Operator.");
    long childNativePtr = (child==null) ? 0 : child.getNativeOperator();
    nativePtr = createNativeOperator(style.ordinal(), childNativePtr);
    // TODO: other parameters
  }
  
  public native long createNativeOperator(int style, long child);
  
}
