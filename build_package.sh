#!/bin/bash

set -e

VERSION=$(cat version)
BRIEF_VERSION=$(cat version | sed -E 's/([0-9]\.[0-9])\.[0-9]/\1/')

mkdir -p release/
rm -rf tmp/*
mkdir -p tmp/$BRIEF_VERSION/

cd dist/
for zimbraVersion in *;
do
  cp "${zimbraVersion}/zal.jar" "../tmp/$BRIEF_VERSION/zal-${VERSION}-${zimbraVersion}.jar"
done;

cd ../tmp/
tar czf "../release/zal-$VERSION-all.tar.gz" *
cd ..
rm -rf tmp/
