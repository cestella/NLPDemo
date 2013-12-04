package com.hortonworks.pig;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.hortonworks.nlp.*;
import org.apache.pig.EvalFunc;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.*;
import org.apache.pig.impl.logicalLayer.schema.Schema;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

/**
 * Created with IntelliJ IDEA.
 * User: cstella
 * Date: 12/3/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class GET_SIP extends EvalFunc<DataBag>
{
    TupleFactory mTupleFactory = TupleFactory.getInstance();
    BagFactory mBagFactory = BagFactory.getInstance();

    private static class Converter implements Function<Tuple, List<Bigram<Word>> >
    {
        Statistics<Bigram<String>> bigramStatistics ;
        Statistics<String> unigramStatistics ;
        public Converter( Statistics<Bigram<String>> bigramStatistics
                        , Statistics<String> unigramStatistics
        )
        {
            this.bigramStatistics = bigramStatistics;
            this.unigramStatistics = unigramStatistics;
        }

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
     * This callback method must be implemented by all subclasses. This
     * is the method that will be invoked on every Tuple of a given dataset.
     * Since the dataset may be divided up in a variety of ways the programmer
     * should not make assumptions about state that is maintained between
     * invocations of this method.
     *
     * @param input the Tuple to be processed.
     * @return result, of type T.
     * @throws java.io.IOException
     */
    @Override
    public DataBag exec(Tuple input) throws IOException
    {
        Statistics<Bigram<String>> bigramStatistics = new Statistics<Bigram<String>>();
        Statistics<String> unigramStatistics = new Statistics<String>();
        Converter converter = new Converter(bigramStatistics,unigramStatistics);
        Scorer scorer = new ScaledMutualInformationScorer();
        DataBag output = mBagFactory.newDefaultBag();
        DataBag inputBag = (DataBag)input.get(0);
        Iterable<Bigram<String>> sips =
                Iterables.limit(
        NLPUtil.getStatisticallyImprobableBigrams( Iterables.transform(inputBag, converter)
                                                 , Predicates.<Bigram<Word>>alwaysTrue()
                                                 , bigramStatistics
                                                 , unigramStatistics
                                                 , scorer
                                                 )
                , 10
                )
                ;
        for(Bigram<String> sip : sips)
        {
            Tuple t = mTupleFactory.newTuple(3);
            t.set(0, sip.getLeft());
            t.set(1, sip.getRight());
            t.set(2, sip.getScore());
            output.add(t);
        }
        return output;
    }
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
