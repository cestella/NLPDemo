#!/bin/bash

BASE_DIR=$1
hadoop fs -mkdir $BASE_DIR/data
hadoop fs -put data/sentences.dat $BASE_DIR/data
