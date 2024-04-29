#include "mostdb-core/jni/team_ictdb_mostdb_insert_Buffer_AsyncFlush.h"
#include "mostdb-core/jni/team_ictdb_mostdb_query_WeavingScan.h"
#include "mostdb-core/jni/team_ictdb_mostdb_query_DualModeFilter.h"
#include "mostdb-core/jni/team_ictdb_mostdb_query_DualModeAggregate.h"
#include "mostdb-core/jni/team_ictdb_mostdb_query_DualModeOutput.h"
#include "mostdb-core/compression/Most.h"
#include "mostdb-core/query/Operators.h"

using namespace most;

JNIEXPORT void JNICALL
Java_team_ictdb_mostdb_insert_Buffer_00024AsyncFlush_most
(JNIEnv* env, jobject obj, jlong globalStart, jint gap, jboolean absError,
 jfloat errBound, jlong inputAddr, jlong outputAddr, jint lineCount) {
  cmpr::Most most(globalStart, gap, absError, errBound);
  most.Compress((most::cmpr::TVPair*)inputAddr, lineCount, (char*)outputAddr);
}

JNIEXPORT jlong JNICALL
Java_team_ictdb_mostdb_query_WeavingScan_createNativeOperator
(JNIEnv* env, jobject obj, jint style, jlong child) {
  query::WeavingScan* weavingScan = new query::WeavingScan(
      style, (query::Operator*)child
  );
  return (jlong)weavingScan;
}

JNIEXPORT jlong JNICALL
Java_team_ictdb_mostdb_query_DualModeFilter_createNativeOperator
    (JNIEnv* env, jobject obj, jint style, jlong child) {
  query::DualModeFilter* filter = new query::DualModeFilter(
      style, (query::Operator*)child
      );
  return (jlong)filter;
}

JNIEXPORT jlong JNICALL
Java_team_ictdb_mostdb_query_DualModeAggregate_createNativeOperator
    (JNIEnv* env, jobject obj, jint style, jlong child) {
  query::DualModeAggregate* aggregate = new query::DualModeAggregate(
      style, (query::Operator*)child
  );
  return (jlong)aggregate;
}

JNIEXPORT jlong JNICALL
Java_team_ictdb_mostdb_query_DualModeOutput_createNativeOperator
    (JNIEnv* env, jobject obj, jint style, jlong child) {
  query::DualModeOutput* output = new query::DualModeOutput(
      style, (query::Operator*)child
  );
  return (jlong)output;
}

