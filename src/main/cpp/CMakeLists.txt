cmake_minimum_required(VERSION 3.0)
project(mostdb_core VERSION 0.0.1)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Werror -Wno-unused")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-exceptions -fno-rtti")

if (CMAKE_BUILD_TYPE STREQUAL "Debug")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O2 -fno-strict-aliasing")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -g -fsanitize=address -fno-omit-frame-pointer")
elseif (CMAKE_BUILD_TYPE STREQUAL "Release")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -O3 -fno-strict-aliasing")
    set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -O3 -fno-strict-aliasing") # no lto
endif()

file(GLOB_RECURSE MOSTDB_CORE_SRC lib/*.cpp)
include_directories("include")
set(JAVA_HOME $ENV{JAVA_HOME})
message("JAVA_HOME: ${JAVA_HOME}")
include_directories("${JAVA_HOME}/include")
if (CMAKE_SYSTEM_NAME STREQUAL "Linux")
    include_directories("${JAVA_HOME}/include/linux")
elseif(CMAKE_SYSTEM_NAME STREQUAL "Darwin")
    include_directories("${JAVA_HOME}/include/darwin")
endif()

set(MOSTDB_CORE_LIB_INSTALL_DIR "/usr/local/lib")
add_library(mostdb_core_dynamic SHARED ${MOSTDB_CORE_SRC})
set_target_properties(mostdb_core_dynamic PROPERTIES OUTPUT_NAME "mostdb_core")
install(TARGETS mostdb_core_dynamic LIBRARY DESTINATION ${MOSTDB_CORE_LIB_INSTALL_DIR})

## for static lib
#add_library(mostdb_core_static STATIC ${MOSTDB_CORE_SRC})
#set_target_properties(mostdb_core_static PROPERTIES OUTPUT_NAME "mostdb_core")
#install(TARGETS mostdb_core_static ARCHIVE DESTINATION ${MOSTDB_CORE_LIB_INSTALL_DIR})