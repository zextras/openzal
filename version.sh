#!/bin/bash 

mvn help:evaluate -Dexpression=project.version -q -DforceStdout 2> /dev/null
