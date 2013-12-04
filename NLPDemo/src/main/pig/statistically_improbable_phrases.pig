register '../NLPDemo-1.0-SNAPSHOT.jar'
DEFINE GET_SIP com.hortonworks.pig.GET_SIP;

SENTENCES = LOAD '$input' USING PigStorage('\u0001') as 
            ( diag_code:chararray
            , type:chararray
            , doc:chararray
            );

CODE_GRP = group SENTENCES by diag_code;
SIP = foreach CODE_GRP {
  DOCS = foreach SENTENCES generate doc;
  generate group as diag_code,GET_SIP(DOCS) as sips;
};

STORE SIP into '$output' using PigStorage('\u0001');
