#include <limits>
#include "mostdb-core/compression/Most.h"

using namespace most;
using namespace most::cmpr;

Most::Most(time_t globalStart, time_diff_t gap, bool absError, float errBound):
mGlobalStart(globalStart), mGap(gap), mAbsError(absError), mErrBound(errBound) {
  /* empty */
}

void Most::Compress(TVPair* input, uint32_t length, char* output) {
  MostOutput* mostOutput = (MostOutput*)output;
  mostOutput->mSegCnt = 0;
  mostOutput->mOutCnt = 0;

  int start = 0;
  int next = 1;
  int splitter = -1;
  slope_t lowerSlope = std::numeric_limits<float>::lowest();
  slope_t upperSlope = std::numeric_limits<float>::max();
  while (next < length) {
    slope_t curLowerSlope = 0; // TODO
    curLowerSlope.Inc();
    slope_t curUpperSlope = 0; // TODO
    if (curLowerSlope <= upperSlope && curUpperSlope >= lowerSlope) {
      // Segment still alive!
      if (splitter > 0) { // put splitter into outliers
        *(mostOutput->GetOutlier(mostOutput->mOutCnt)) = {
            .mTime = 0, // TODO
            .mQuantizedError = 0 // TODO
        };
        mostOutput->mOutCnt ++;
        splitter = -1;
      }
      lowerSlope = (curLowerSlope >= lowerSlope) ? curLowerSlope : lowerSlope;
      upperSlope = (curUpperSlope <= upperSlope) ? curUpperSlope : upperSlope;
      next ++;
    } else if (splitter < 0) { // try skip the splitter
      splitter = next;
      next ++;
    } else { // too many splitters
      if (splitter - start >= mMinSegLen) { // new segment
        *(mostOutput->GetSegment(mostOutput->mSegCnt)) = {
            .mStart = 0, // TODO
            .mSlope = 0, // TODO
            .mIntercept = 0 // TODO
        };
        mostOutput->mSegCnt ++;
        start = splitter;
      } else { // Segment is too short! Start is an outlier!
        *(mostOutput->GetOutlier(mostOutput->mOutCnt)) = {
            .mTime = 0, // TODO
            .mQuantizedError = 0 // TODO
        };
        mostOutput->mOutCnt ++;
        start ++;
      }
      next = start + 1;
      splitter = -1;
      lowerSlope = std::numeric_limits<slope_t>::min();
      upperSlope = std::numeric_limits<slope_t>::max();
    }
  }
}
