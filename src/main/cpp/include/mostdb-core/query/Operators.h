#ifndef MOSTDB_CORE_QUERY_OPERATORS_H
#define MOSTDB_CORE_QUERY_OPERATORS_H

#include "mostdb-core/query/Operator.h"

namespace most {

namespace query {

class WeavingScan : public AbstractOperator {
public:
  WeavingScan(int style, Operator* child);
  void Weave(char* input);
  OperatorResult GetNext() override;
  void Print(uint32_t indent) override;
private:
  // TODO: Table
}; // WeavingScan

class DualModeFilter : public AbstractOperator {
public:
  DualModeFilter(int style, Operator* child);
  OperatorResult GetNext() override;
  void Print(uint32_t indent) override;
}; // DualModeFilter

class DualModeAggregate : public AbstractOperator {
public:
  DualModeAggregate(int style, Operator* child);
  OperatorResult GetNext() override;
  void Print(uint32_t indent) override;
}; // DualModeAggregate

class DualModeOutput : public AbstractOperator {
public:
  DualModeOutput(int style, Operator* child);
  OperatorResult GetNext() override;
  void Print(uint32_t indent) override;
}; // DualModeOutput

} // namespace most::query

} // namespace most

#endif // MOSTDB_CORE_QUERY_OPERATORS_H
