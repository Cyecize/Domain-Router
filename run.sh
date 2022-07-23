#!/bin/bash

libraries="."

cd lib
for FILE in *; do 
libraries="$libraries:./lib/$FILE" 
done

echo "$libraries"

cd ../

java  -cp "$libraries" com.cyecize.domainrouter.AppStartUp
