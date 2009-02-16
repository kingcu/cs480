#!/bin/bash

rm *.class ; javac Asgn3.java
rm diffs
for file in `ls tests/asgn3/* | grep -v "\.[so]$"`
do
	java Asgn3 $file > $file.o
	diff -u $file.o $file.s >> diffs
done

