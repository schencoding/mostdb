package team.ictdb.mostdb.query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import team.ictdb.mostdb.query.Query.ResultStyle;

public class DualModeFilter extends TransformedQuery.AbstractOpWrapper {
  
  private static final Logger LOGGER = LogManager.getLogger();

  public DualModeFilter(ResultStyle style) {
    super(style);
  }
  
  @Override
  public void buildOperator() {
    LOGGER.info("Build Dual-Mode Filter Operator.");
    long childNativePtr = (child==null) ? 0 : child.getNativeOperator();
    nativePtr = createNativeOperator(style.ordinal(), childNativePtr);
    // TODO: other parameters
  }
  
  public native long createNativeOperator(int style, long child);
}
