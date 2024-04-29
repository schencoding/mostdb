#ifndef MOSTDB_CORE_CMPR_COMPRESSOR_H
#define MOSTDB_CORE_CMPR_COMPRESSOR_H

#include <cstdint>

namespace most {

namespace cmpr {

using time_t = uint64_t;
using time_diff_t = int32_t;
using value_t = double;

struct TVPair {
  time_t t;
  value_t v;
}; // TVPair

#ifndef INTERFACE
#define INTERFACE
#endif

INTERFACE class Compressor {
public:
  virtual void Compress(TVPair* input, uint32_t length, char* output) = 0;

}; // interface Compressor

} // namespace most::cmpr

} // namespace most

#endif // MOSTDB_CORE_CMPR_COMPRESSOR_H
