#ifndef MOSTDB_CORE_CMPR_MOST_H
#define MOSTDB_CORE_CMPR_MOST_H

#include "mostdb-core/compression/Compressor.h"

namespace most {

namespace cmpr {

using qerror_t = int32_t; // quantized error
using intercept_t = float;

struct slope_t { // float32 with 16 trailing zeros
  float val;

  inline slope_t(float value = 0): val((float)(
      *((uint32_t*)&value) & 0xffff0000
      )) {}
  inline void Inc() { *((uint32_t*)&val) += 0x00010000; }
  inline bool operator <= (const slope_t& other) { return val <= other.val; }
  inline bool operator >= (const slope_t& other) { return val >= other.val; }
}; // slope_t 4B

//struct MiniTVPair {
//  time_diff_t t;
//  float v;
//}; // MiniTVPair 8B
//
//struct AnotherSegment {
//  MiniTVPair mStart;
//  MiniTVPair mEnd;
//}; // AnotherSegment 16B

struct Segment {
  time_diff_t mStart;
  slope_t mSlope;
  intercept_t mIntercept;
}; // Segment 12B

struct Outlier {
  time_diff_t mTime;
  qerror_t mQuantizedError;
}; // Outlier 8B

struct MostOutput {
  static const uint32_t RESULT_SZ = 256*1024;
  uint32_t mSegCnt;
  uint32_t mOutCnt;
  char mData[0];

  inline Segment* GetSegment(uint32_t idx) { return ((Segment*)mData)+idx; }
  inline Outlier* GetOutlier(uint32_t idx) {
    return ((Outlier*)(((char*)this)+RESULT_SZ))-idx-1;
  }
}; // MostOutput

class Most : public Compressor {
public:
  Most(time_t, time_diff_t, bool, float);
  void Compress(TVPair* input, uint32_t length, char* output) override;

private:
  time_t mGlobalStart;
  time_diff_t mGap;
  bool mAbsError;
  float mErrBound;
  uint32_t mMinSegLen = 5;
}; // Most

} // namespace most::cmpr

} // namespace most

#endif // MOSTDB_CORE_CMPR_MOST_H
