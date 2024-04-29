#ifndef MOSTDB_CORE_QUERY_OPERATOR_H
#define MOSTDB_CORE_QUERY_OPERATOR_H

#include <cstdint>
#include <string>
#include <vector>
#include <unordered_map>

namespace most {

namespace query {

struct GlobalSegment {

}; // GlobalSegment

struct GlobalOutlier {

}; // GlobalOutlier

class OperatorResult {
public:
  GlobalSegment mSegment;
  std::vector<GlobalOutlier> mOutliers;
}; // OperatorResult

enum class ResultStyle : int32_t {
  DEFAULT, E, EB, EBS
}; // enum ResultStyle

extern std::unordered_map<ResultStyle, std::string> gResultStyle2String;

#define INTERFACE
#define ABSTRACT

INTERFACE class Operator {
public:
  virtual OperatorResult GetNext() = 0;
  virtual ResultStyle GetStyle() = 0;
  virtual Operator* GetChild() = 0;
  virtual void Print(uint32_t indent) = 0;
}; // interface Operator

ABSTRACT class AbstractOperator : public Operator {
public:
  AbstractOperator(int style, Operator* child);
  ResultStyle GetStyle() override;
  Operator* GetChild() override;

protected:
  ResultStyle mStyle = ResultStyle::DEFAULT;
  Operator* mChild = nullptr;
}; // AbstractOperator

} // namespace most::query

} // namespace most

#endif // MOSTDB_CORE_QUERY_OPERATOR_H
