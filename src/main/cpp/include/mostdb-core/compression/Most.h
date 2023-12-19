#ifndef MOSTDB_CORE_CMPR_MOST_H
#define MOSTDB_CORE_CMPR_MOST_H

#include "mostdb-core/compression/Compressor.h"

namespace most {

namespace cmpr {

class Most : public Compressor {
public:
  Most(time_t, time_diff_t);
  void Compress(TVPair* input, uint32_t length, char* output) override;

private:
  time_t mGlobalStart;
  time_diff_t mGap;

}; // Most

} // namespace most::cmpr

} // namespace most

#endif // MOSTDB_CORE_CMPR_MOST_H
