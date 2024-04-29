package team.ictdb.mostdb.insert;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import team.ictdb.mostdb.schema.Schema;

public class Buffer {
  
  private static final Logger LOGGER = LogManager.getLogger();
  public static final int CAPACITY = 16384; // count of doubles
  public static final int MEMTABLE = 4;
  public static final int LINE_SIZE = Long.BYTES + Double.BYTES;
  public static final int RESULT_SZ = 256*1024;
  
  public static Map<String, Buffer[]> buffersMap = new HashMap<>();
  private static ThreadPoolExecutor threadPool;
  private static Method directByteBufferAddressMethod;
  
  public static void prepare() {
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    threadPool = new ThreadPoolExecutor(
        8, /* corePoolSize */
        8, /* maximumPoolSize */
        10, /* keepAliveTime */
        TimeUnit.SECONDS, /* TimeUnit */
        workQueue
        );
    LOGGER.info("Buffer.threadPool constructed.");
    try {
      Class<?> clazz = Class.forName("java.nio.DirectByteBuffer");
      directByteBufferAddressMethod = clazz.getMethod("address");
      directByteBufferAddressMethod.setAccessible(true);
    } catch (Exception e) {
      LOGGER.error("Reflection error", e);
      System.exit(1);
    }
    LOGGER.info("java.nio.DirectByteBuffer reflected.");
  }
  
  public static void shutdown() throws Exception {
    for (Buffer[] buffers : buffersMap.values()) {
      for (Buffer buffer : buffers) {
        synchronized (buffer) {
          buffer.asyncFlush();
        }
      }
    }
    threadPool.shutdown();
    while (!threadPool.awaitTermination(100, TimeUnit.MICROSECONDS)) {}
  }
  
  private final String seriesID;
  private final Schema schema;
  private final float errorBound;
  
  public Schema getSchema() { return schema; }
  
  private ByteBuffer page, result;
  private long pageAddr, resultAddr;
  private volatile VolatileBool[] flushing = new VolatileBool[MEMTABLE];
  //private AtomicIntegerArray flushing = new AtomicIntegerArray(MEMTABLE);
  private Object[] flushLocks = new Object[MEMTABLE];
  private int curMemTableIdx = 0;
  private int lineCount = 0;

  public Buffer(String tableName, String[] tags, String measName) 
      throws Exception {
    StringBuilder builder = new StringBuilder();
    builder.setLength(0);
    builder.append(tableName);
    for (String tag : tags) {
      builder.append(",").append(tag);
    }
    builder.append(",").append(measName);
    this.seriesID = builder.toString();
    this.schema = Schema.tables.get(tableName);
    this.errorBound = schema.getErrorBound()[schema.getMeasId(measName)];
    this.page = ByteBuffer.allocateDirect(MEMTABLE * CAPACITY * LINE_SIZE);
    this.page.order(ByteOrder.nativeOrder());
    this.pageAddr = (long)directByteBufferAddressMethod.invoke(page);
    this.result = ByteBuffer.allocateDirect(MEMTABLE * RESULT_SZ);
    this.result.order(ByteOrder.nativeOrder());
    this.resultAddr = (long)directByteBufferAddressMethod.invoke(result);
    for (int i = 0; i < MEMTABLE; i ++) {
      this.flushing[i] = new VolatileBool();
      this.flushing[i].elem = false;
      this.flushLocks[i] = new Object();
    }
  }
  
  private void asyncFlush() { // caller within synchronized(this)
    if (lineCount == 0) {
      return;
    }
    flushing[curMemTableIdx].elem = true;
    threadPool.submit(new AsyncFlush(curMemTableIdx, lineCount));
    lineCount = 0;
    curMemTableIdx = (curMemTableIdx + 1) % MEMTABLE;
  }
  
  public void input(long timestamp, double value) throws Exception {
    synchronized (this) {
      while (flushing[curMemTableIdx].elem) { // don't release outer lock
        synchronized (flushLocks[curMemTableIdx]) {
          flushLocks[curMemTableIdx].wait();
        }
      }
      int offset = (curMemTableIdx * CAPACITY + lineCount) * LINE_SIZE;
      page.putLong(offset, timestamp);
      page.putDouble(offset + Long.BYTES, value);
      lineCount ++;
      if (lineCount == CAPACITY) {
        asyncFlush();
      }
    }
  }

  public class AsyncFlush implements Runnable {
    
    int memTableIndex;
    int lineCount;
    
    AsyncFlush(int memTableIndex, int lineCount) {
      this.memTableIndex = memTableIndex;
      this.lineCount = lineCount;
    }
    
    @Override
    public void run() {
      long inputAddr = pageAddr + memTableIndex * CAPACITY * LINE_SIZE;
      long outputAddr = resultAddr + memTableIndex * RESULT_SZ;
      most(schema.globalStart, schema.gap, schema.isAbsoluteError(),
          errorBound, inputAddr, outputAddr, lineCount);
      LOGGER.debug(seriesID+" "+memTableIndex);
      // TODO: to influxDB
      flushing[memTableIndex].elem = false;
      synchronized (flushLocks[memTableIndex]) {
        flushLocks[memTableIndex].notifyAll();
      }
    }
    
    public native void most(long globalStart, int gap, boolean absErr, 
        float errBound, long inputAddr, long outputAddr, int lineCount);
  }
  
  static class VolatileBool {
    public volatile boolean elem = false;
  }
  
}
