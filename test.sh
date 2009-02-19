#!/bin/bash

rm *.class ; javac Asgn4.java
rm diffs;

for file in `ls tests/asgn4/* | grep -v "\.[so]$"`
do
	java Asgn4 $file > $file.o
	diff -u $file.o $file.s >> diffs
done

rm tests/asgn4/*.o 
