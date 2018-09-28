#!/bin/bash

set -e
ROOT=$(dirname $0)

mkdir -p ${ROOT}/tmp/build/
javac ${ROOT}/build/*.java -d ${ROOT}/tmp/build/
java -cp ${ROOT}/tmp/build/ ZalBuilder $@
