#include "mostdb-core/query/Operator.h"

using namespace most;
using namespace most::query;

std::unordered_map<ResultStyle, std::string> most::query::gResultStyle2String = {
    {ResultStyle::DEFAULT, "default"},
    {ResultStyle::E, "E"},
    {ResultStyle::EB, "EB"},
    {ResultStyle::EBS, "EBS"},
};

AbstractOperator::AbstractOperator(int style, Operator* child):
mStyle(static_cast<ResultStyle>(style)), mChild(child) {
  /* empty */
}

ResultStyle AbstractOperator::GetStyle() { return mStyle; }
Operator* AbstractOperator::GetChild() { return mChild; }