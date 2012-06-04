#!/bin/bash

echo -n "Enter source path: "
read src

echo -n "Enter password for ZIP files: "
read pass

echo
echo

echo "Deleting ald archives."
rm $src/*.zip
echo

#enable for loops over items with spaces in their name
IFS=$'\n'
 
for dir in `ls "$src/"`
do
  if [ -d "$src/$dir" ]; then
	echo "Creating archive for directory $dir"
	zip -r -9 -e -P $pass --include=*.csv $src/$dir.zip $src/$dir/
  fi
done

echo

echo "Creating ZIP File for upload"

zip -r -9 --include=*.zip $src/_upload.zip $src/

echo
echo "Archive created."
echo "Please upload the file $src/_upload.zip to the HRZ media server"
echo "at https://medienportal.mmag.hrz.tu-darmstadt.de/"