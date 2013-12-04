#!/bin/bash

DIAG_CODE=$1
DATA_FILE=$2

NORMALIZED_CODE=$(echo $DIAG_CODE | sed 's/\.//g')
DESCRIPTION=$(cat data/CMS31_DESC_LONG_DX.txt | grep "^$NORMALIZED_CODE " | cut -c 7- )
echo "DIAG_CODE"
echo "-----------------"
echo "DESCRIPTION"
echo "================="
echo $DESCRIPTION
echo "================="
echo "BIGRAMS w/ SCORE"
echo "================="

grep "^$DIAG_CODE" $DATA_FILE | sed 's/{//g' | sed 's/}//g' | sed 's/(//g' | awk -F '{print $2}' | tr ')' '\n' | sed 's/)//g' |  sed 's/^,//g' | tr ',' '\t'
