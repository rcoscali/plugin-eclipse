#!/bin/bash

echo "Copying links to archive (Linux only !)"

LD_LIBRARY_PATH=./bin/lib
export LD_LIBRARY_PATH

ldd bin/clang-tidy

clang_tidy_deps=`ldd bin/clang-tidy | sed '/=>.*$/ !d' | sed s-"=> ./bin/lib/.*$"-- | sed '/=>.*$/ d'`
for lib in $clang_tidy_deps
do
  echo $lib
  zip -uv target/*.jar "bin/lib/$lib"
done
echo "Links copied to archive :-)"
