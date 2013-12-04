#!/bin/bash

DATA_FILE=$1

for DIAG_CODE in $(cat $DATA_FILE | awk -F '{print $1}');do

	NORMALIZED_CODE=$(echo $DIAG_CODE | sed 's/\.//g')
	DESCRIPTION=$(cat data/CMS31_DESC_LONG_DX.txt | grep "^$NORMALIZED_CODE " | cut -c 7- )
	echo "$DIAG_CODE -- $DESCRIPTION"
done
