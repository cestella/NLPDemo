package com.hortonworks.pig;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.hortonworks.nlp.*;
import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.*;
import org.apache.pig.impl.logicalLayer.schema.Schema;

import java.io.IOException;
import java.util.List;

/**
 * UDF to take a bag of documents and output a bag of statistically improbable bigrams.
 *
 * User: cstella
 * Date: 12/3/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class GET_SIP extends EvalFunc<DataBag>
{
    TupleFactory mTupleFactory = TupleFactory.getInstance();
    BagFactory mBagFactory = BagFactory.getInstance();

    /**
     * Convenience function to convert a Tuple whose first element is a document and converting it into a list of Bigrams
     */
    private static class Converter implements Function<Tuple, List<Bigram<Word>> >
    {
        Statistics<Bigram<String>> bigramStatistics ;
        Statistics<String> unigramStatistics ;

        /**
         * Converter constructor.
         *
         * Note: this function is not stateless.
         * Summary statistics are aggregated, so don't reuse instances of this class unless they're
         * associated with the same dataset.
         *
         * @param bigramStatistics Summary statistics container for bigrams (updated when apply is called)
         * @param unigramStatistics Summary statistics container for unigrams (updated when apply is called)
         */
        public Converter( Statistics<Bigram<String>> bigramStatistics
                        , Statistics<String> unigramStatistics
        )
        {
            this.bigramStatistics = bigramStatistics;
            this.unigramStatistics = unigramStatistics;
        }

        /**
         *  Convert Tuple objects to lists of bigrams.
         *
         * @param input A tuple whose first element is a document to process
         * @return A list of bigrams from the document contained in the first element of the input tuple
         */
        public List<Bigram<Word>> apply(org.apache.pig.data.Tuple input) {
            List<Word> words = null;
            try {
                words = NLPUtil.parseSentence((String) input.get(0), unigramStatistics);
            } catch (ExecException e) {
                throw new RuntimeException(e);
            }
            List<Bigram<Word>> bigrams = NLPUtil.getBigramsPerSentence(words, bigramStatistics);
            return bigrams;
        }
    }

    /**
     * Returns a sorted list of statistically improbable bigrams, sorted descending.
     *
     * @param listOfDocumentBigrams A list of lists of bigrams.  The inner list is the bigrams for a given document.
     * @param scorer The scorer to use to determine ranking
     * @param stopwordFilter The stopword filter applied to the bigram.
     * @param bigramStatistics Summary statistics for bigrams across the whole set of documents in listOfDocumentBigrams
     * @param unigramStatistics Summary statistics for words (aka unigrams) across the whole set of documents
     * @return
     */
    public static Iterable<Bigram<String>> getStatisticallyImprobableBigrams(Iterable<List<Bigram<Word>>> listOfDocumentBigrams
            , Scorer scorer
            , Predicate<Bigram<Word>> stopwordFilter
            , Statistics<Bigram<String>> bigramStatistics
            , Statistics<String> unigramStatistics
    )
    {

       /*
        *  FILL ME IN!!!!!
        */
        return null;
    }
    /**
     * This UDF takes in a tuple containing a bag of documents to compute the statistically
     * improbable bigrams.
     *
     * @param input the Tuple to be processed.
     * @return result, of type T.
     * @throws java.io.IOException
     */
    @Override
    public DataBag exec(Tuple input) throws IOException
    {

        DataBag output = mBagFactory.newDefaultBag();
        /*
         * Bag of tuples, each tuple's first element contains a document.  The bag forms the document set
         * that we are computing statistically improbable bigrams from.
         */
        DataBag inputBag = (DataBag)input.get(0);

        /*
         * Create summary statistics containers for this document set
         */
        Statistics<Bigram<String>> bigramStatistics = new Statistics<Bigram<String>>();
        Statistics<String> unigramStatistics = new Statistics<String>();

        /*
         *  Convenience function to convert from tuples into lists of bigrams using NLPUtil
         */
        Converter converter = new Converter(bigramStatistics,unigramStatistics);

        /*
         * Using the scaled mutual information scorer to determine ranking of bigrams
         */
        Scorer scorer = new ScaledMutualInformationScorer();

        //Currently no stopword filter is used
        Predicate<Bigram<Word>> stopwordFilter = Predicates.<Bigram<Word>>alwaysTrue();

        /*
         * Convert the input bag of tuples into an iterable of bigram lists using the Converter function
         */
        Iterable<List<Bigram<Word>>> documentsAsBigramLists = Iterables.transform(inputBag, converter);

        /**
         * Now that we have the data processed and transformed into bigrams, we can figure out which of those
         * bigrams are statistically important.
         */
        Iterable<Bigram<String>> sips =
                getStatisticallyImprobableBigrams(documentsAsBigramLists
                                                 , scorer
                                                 , stopwordFilter
                                                 , bigramStatistics
                                                 , unigramStatistics
                                                 );

        /*
         * Output the top 10 as tuples and add them to the bag.
         */
        for(Bigram<String> sip : Iterables.limit(sips, 10))
        {
            Tuple t = mTupleFactory.newTuple(3);
            t.set(0, sip.getLeft());
            t.set(1, sip.getRight());
            t.set(2, sip.getScore());
            output.add(t);
        }
        return output;
    }

    /**
     * Create the schema.  The output is a bag of tuples, each of which have 3 entries:
     * left, right and score, which forms a ranked bigram.
     *
     * @param input
     * @return
     */
    public Schema outputSchema(Schema input) {
        try
        {
            Schema tupleSchema = new Schema();
            tupleSchema.add(new Schema.FieldSchema("left", DataType.CHARARRAY));
            tupleSchema.add(new Schema.FieldSchema("right", DataType.CHARARRAY));
            tupleSchema.add(new Schema.FieldSchema("score", DataType.DOUBLE));


            Schema.FieldSchema tupleFs;
            tupleFs = new Schema.FieldSchema("sip", tupleSchema,
                    DataType.TUPLE);

            Schema bagSchema = new Schema(tupleFs);
            bagSchema.setTwoLevelAccessRequired(true);
            Schema.FieldSchema bagFs = new Schema.FieldSchema(
                    "sips" ,bagSchema, DataType.BAG);

            return new Schema(bagFs);

        }catch (Exception e){
            return null;
        }
    }
}
