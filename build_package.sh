#!/bin/bash

set -e

VERSION=$(cat version)

mkdir -p tmp/
mkdir -p release/
echo rm -rf tmp/*

cd dist/
for zimbraVersion in *;
do
  cp "${zimbraVersion}/zal.jar" "../tmp/${VERSION}-${zimbraVersion}.jar"
done;

cd ../tmp/
tar czf "../release/zal-$VERSION-all.tar.gz" *
cd ..
rm -rf tmp/
