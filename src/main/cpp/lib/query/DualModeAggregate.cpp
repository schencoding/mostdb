#include <cstdio>
#include "mostdb-core/query/Operators.h"

using namespace most;
using namespace most::query;

DualModeAggregate::DualModeAggregate(int style, Operator* child):
AbstractOperator(style, child) {

}

OperatorResult DualModeAggregate::GetNext() {
  return {};
}

void DualModeAggregate::Print(uint32_t indent) {
  for (uint32_t i = 0; i < indent; i ++) printf("  ");
  printf("DualModeAggregate <%s mode>\n", gResultStyle2String[mStyle].c_str());
  if (mChild) {
    mChild->Print(indent+1);
  }
}