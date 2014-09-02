#!/bin/bash

cat > src/java/org/openzal/zal/ZalBuildInfo.java <<EOF
package org.openzal.zal;

public class ZalBuildInfo
{
    public final static String COMMIT="$(git rev-parse HEAD)";
    public final static String VERSION="$(cat version)";
}
EOF
