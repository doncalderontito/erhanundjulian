#!/bin/sh

echo "Updating to the latest version from SVN..."
svn update
echo "----------------------------------------------------"

echo "Deleting old tracebase archives..."
rm -r complete.zip
rm -r incomplete.zip
rm -r synthetic.zip
rm -r tracebase.zip
echo "Copying all training data to temp directory..."
cp -R training temp1
echo "Copying incomplete training data to temp directory..."
cp -R training/1_incomplete temp2
echo "Copying synthetic training data to temp directory..."
cp -R training/2_synthetic temp3
echo "----------------------------------------------------"

echo "Deleting SVN folders in temp directory..."
rm -r ./temp1/1_incomplete
rm -r ./temp1/2_synthetic
rm ./temp1/*
rm -rf `find temp1 -type d -name .svn`
rm -rf `find temp2 -type d -name .svn`
rm -rf `find temp3 -type d -name .svn`
echo "----------------------------------------------------"

echo "Compressing regular traces directory..."
cd temp1
zip -P AllOurTracesAreBelongToYou -r complete.zip .
mv complete.zip ..
cd ..
echo "Compressing incomplete traces directory..."
cd temp2
zip -P AllOurTracesAreBelongToYou -r incomplete.zip .
mv incomplete.zip ..
cd ..
echo "Compressing synthetic traces directory..."
cd temp3
zip -P AllOurTracesAreBelongToYou -r synthetic.zip .
mv synthetic.zip ..
cd ..
echo "----------------------------------------------------"

echo "Bundling all trace files together..."
zip tracebase.zip complete.zip incomplete.zip synthetic.zip 
echo "----------------------------------------------------"

echo "Deleting temporary directories..."
rm -r temp1
rm -r temp2
rm -r temp3
echo "Done!"



