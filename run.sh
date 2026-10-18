#!/bin/sh
set -e
cd "$(dirname "$0")"
rm -rf out && mkdir out
find src -name '*.java' > sources.txt
javac -d out @sources.txt
java -cp out vn.devpro.management.LibraryManagement
