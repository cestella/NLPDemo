package com.hortonworks.nlp;

/**
 * Created with IntelliJ IDEA.
 * User: cstella
 * Date: 12/3/13
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Scorer
{
    public double score( Bigram<String> bigram
                       , Statistics<Bigram<String>> bigramStatistics
                       , Statistics<String> unigramStatistics
                       );

}
