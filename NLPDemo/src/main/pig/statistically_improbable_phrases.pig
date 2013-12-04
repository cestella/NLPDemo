SENTENCES = LOAD '$input' USING PigStorage('\u0001') as 
            ( diag_code:chararray
            , type:chararray
            , doc:chararray
            );
-- fill in the rest
