#!/bin/bash

rm *.class ; javac Asgn5.java
rm diffs;

for file in `ls tests/* | grep -v "\.[so]$"`
do
	java Asgn5 $file > $file.o
	diff -u $file.o $file.s >> diffs
done

rm tests/*.o 
