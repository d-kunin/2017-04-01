#!/usr/bin/env bash

set -eux

GC_LOG_DIR="./gc_logs"
DUMP_DIR="./dumps"
mkdir -p $GC_LOG_DIR $DUMP_DIR

REMOTE_DEBUG="
    -agentlib:jdwp=transport=dt_socket,address=14025,server=y,suspend=n"
MEMORY="
    -Xms512m
    -Xmx512m
    -XX:MaxMetaspaceSize=256m"
GC="
    -XX:+UseConcMarkSweepGC
    -XX:+CMSParallelRemarkEnabled
    -XX:+UseCMSInitiatingOccupancyOnly
    -XX:CMSInitiatingOccupancyFraction=70
    -XX:+ScavengeBeforeFullGC
    -XX:+CMSScavengeBeforeRemark
    -XX:+UseParNewGC"
GC_LOG="
    -verbose:gc
    -Xloggc:$GC_LOG_DIR/gc_pid_%p.log
    -XX:+PrintGCDateStamps
    -XX:+PrintGCDetails
    -XX:+UseGCLogFileRotation
    -XX:NumberOfGCLogFiles=10
    -XX:GCLogFileSize=100M"
JMX="
    -Dcom.sun.management.jmxremote.port=15025
    -Dcom.sun.management.jmxremote.authenticate=false
    -Dcom.sun.management.jmxremote.ssl=false"
DUMP="
    -XX:+HeapDumpOnOutOfMemoryError
    -XX:HeapDumpPath=$DUMP_DIR"

java $REMOTE_DEBUG $MEMORY $GC $GC_LOG $JMX $DUMP -XX:OnOutOfMemoryError="kill -3 %p" -jar target/hw4.jar > jvm.out