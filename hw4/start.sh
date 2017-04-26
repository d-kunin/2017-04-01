#!/usr/bin/env bash
set -ux

GC_LOG_DIR="./gc_logs"
DUMP_DIR="./dumps"
mkdir -p $GC_LOG_DIR $DUMP_DIR

MEMORY="
    -Xms1G
    -Xmx1G
    -XX:MaxMetaspaceSize=256m"
GC_LOG="
    -verbose:gc
    -Xloggc:$GC_LOG_DIR/gc_pid_%p.log
    -XX:+PrintGCDateStamps
    -XX:+PrintGCDetails
    -XX:+UseGCLogFileRotation
    -XX:NumberOfGCLogFiles=10
    -XX:GCLogFileSize=100M"

GC="-XX:+UseSerialGC"
java $MEMORY $GC $GC_LOG -jar target/hw4.jar > SerialGC.out

GC="-XX:+UseParallelGC -XX:+UseParallelOldGC"
java $MEMORY $GC $GC_LOG -jar target/hw4.jar > ParallelGC.out

GC="-XX:+UseConcMarkSweepGC"
java $MEMORY $GC $GC_LOG -jar target/hw4.jar > ConcMarkSweepGC.out

GC="-XX:+UseG1GC"
java $MEMORY $GC $GC_LOG -jar target/hw4.jar > G1GC.out


# for future reference
REMOTE_DEBUG="
    -agentlib:jdwp=transport=dt_socket,address=14025,server=y,suspend=n"
GC="
    -XX:+UseConcMarkSweepGC
    -XX:+CMSParallelRemarkEnabled
    -XX:+UseCMSInitiatingOccupancyOnly
    -XX:CMSInitiatingOccupancyFraction=70
    -XX:+ScavengeBeforeFullGC
    -XX:+CMSScavengeBeforeRemark
    -XX:+UseParNewGC"
JMX="
    -Dcom.sun.management.jmxremote.port=15025
    -Dcom.sun.management.jmxremote.authenticate=false
    -Dcom.sun.management.jmxremote.ssl=false"
DUMP="
    -XX:+HeapDumpOnOutOfMemoryError
    -XX:HeapDumpPath=$DUMP_DIR"
# java $REMOTE_DEBUG $MEMORY $GC $GC_LOG $JMX $DUMP -XX:OnOutOfMemoryError="kill -3 %p" -jar target/hw4.jar > jvm.out