#! /bin/sh
javac -d ./ src/*.java

java Risk smart smart basic basic smart basic

date=$(date +'%m-%d-%Y %H:%M:%S')

cat logs/auxiliar.json | python -m json.tool > logs/${date}.json

# export CLASSPATH=.:/Users/andreesteves/Documents/FEUP/4-year/AIAD/jade/lib/jade.jar:/Users/andreesteves/Documents/FEUP/4-year/AIAD/AIAD/includes/json-simple-1.1.1.jar
