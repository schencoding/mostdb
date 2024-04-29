#include <cstdio>
#include "mostdb-core/query/Operators.h"

using namespace most;
using namespace most::query;

WeavingScan::WeavingScan(int style, Operator* child):
AbstractOperator(style, child) {

}

void WeavingScan::Weave(char* input) {

}

OperatorResult WeavingScan::GetNext() {
  return {};
}

void WeavingScan::Print(uint32_t indent) {
  for (uint32_t i = 0; i < indent; i ++) printf("  ");
  printf("WeavingScan <%s mode>\n", gResultStyle2String[mStyle].c_str());
  if (mChild) {
    mChild->Print(indent+1);
  }
}