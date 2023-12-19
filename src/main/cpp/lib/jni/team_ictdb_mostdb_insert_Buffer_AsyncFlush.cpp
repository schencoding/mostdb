#include "mostdb-core/jni/team_ictdb_mostdb_insert_Buffer_AsyncFlush.h"
#include "mostdb-core/compression/Most.h"

using namespace most;

JNIEXPORT void JNICALL Java_team_ictdb_mostdb_insert_Buffer_00024AsyncFlush_most
(JNIEnv* env, jobject obj, jlong globalStart, jint gap, jlong inputAddr,
 jlong outputAddr, jint lineCount) {
  cmpr::Most most(globalStart, gap);
  most.Compress((most::cmpr::TVPair*)inputAddr, lineCount, (char*)outputAddr);
}

