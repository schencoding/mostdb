#include <cstdio>
#include "mostdb-core/query/Operators.h"

using namespace most;
using namespace most::query;

DualModeFilter::DualModeFilter(int style, Operator* child):
AbstractOperator(style, child) {

}

OperatorResult DualModeFilter::GetNext() {
  return {};
}

void DualModeFilter::Print(uint32_t indent) {
  for (uint32_t i = 0; i < indent; i ++) printf("  ");
  printf("DualModeFilter <%s mode>\n", gResultStyle2String[mStyle].c_str());
  if (mChild) {
    mChild->Print(indent+1);
  }
}