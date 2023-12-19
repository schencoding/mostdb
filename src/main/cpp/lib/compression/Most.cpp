#include "mostdb-core/compression/Most.h"

using namespace most;
using namespace most::cmpr;

Most::Most(time_t globalStart, time_diff_t gap):
mGlobalStart(globalStart), mGap(gap) {
  /* empty */
}

void Most::Compress(TVPair* input, uint32_t length, char* output) {

}
