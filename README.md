# MostDB

MOST: Model-Based Compression with Outlier Storage for Time Series Data

### I. Requirements
1. InfluxDB v2, both client and server
2. OpenJDK v11
3. A C++17 compiler
4. CMake v3+
5. Maven v3+

### II. Before building: Create InfluxDBv2 token
1. Start the InfluxDBv2 server.
2. Create user(default 'mostdb'), password(default 'mostdb123'), 
bucket(default 'mostdb'), org(default 'mostdb') for MostDB.
3. Open http://localhost:8086 on browser and copy your token to 

```java
// in src/main/java/team/ictdb/mostdb/MostDB.java
private char[] token = ("<Your token here>").toCharArray();
```

### III. Building from source
#### 1. Build and install libmostdb_core

```shell
export JAVA_HOME="..." #if necessary
cd src/main/cpp
cmake -B build .
cd build && make && make install
```

#### 2. Build and install MostDB

```shell
# return to root directory of this project
mvn compile
mvn test
mvn package
mvn install
```

Then you can use MostDB in other maven projects.


### Attention
This is a prototype implementation. We are still working on code refining.

