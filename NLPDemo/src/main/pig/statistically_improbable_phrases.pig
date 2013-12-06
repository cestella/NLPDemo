DEFINE GET_SIP com.hortonworks.pig.GET_SIP;

-- Load the documents from HDFS
SENTENCES = LOAD '$input' USING PigStorage('\u0001') as
            ( diag_code:chararray
            , type:chararray
            , doc:chararray
            );

-- Grouped by diagnosis code
CODE_GRP = group SENTENCES by diag_code;


-- Create a relation, SIP with the following schema:
--    diag_code: chararray
--    sips: bag: { left:chararray, right:chararray, score:double }
-- where sips is the bag of statistically improbable bigrams

-- FILL ME IN!!!
SIP = ...

rmf $output
STORE SIP into '$output' using PigStorage('\u0001');
