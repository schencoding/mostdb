#include <cstdio>
#include "mostdb-core/query/Operators.h"

using namespace most;
using namespace most::query;

DualModeOutput::DualModeOutput(int style, Operator* child):
AbstractOperator(style, child) {

}

OperatorResult DualModeOutput::GetNext() {
  return {};
}

void DualModeOutput::Print(uint32_t indent) {
  for (uint32_t i = 0; i < indent; i ++) printf("  ");
  printf("DualModeOutput <%s mode>\n", gResultStyle2String[mStyle].c_str());
  if (mChild) {
    mChild->Print(indent+1);
  }
}