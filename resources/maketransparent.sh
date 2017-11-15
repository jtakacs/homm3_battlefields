#!/bin/bash

mkdir y
for file in *.png 
do 
    convert -define png:color-type='2' -depth 16 -alpha set -transparent "#00ffff" "$file" "y/$file" 
done

