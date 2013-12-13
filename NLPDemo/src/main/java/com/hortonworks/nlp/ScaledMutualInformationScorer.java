package com.hortonworks.nlp;

/**
 * Pairwise mutual information scorer, scaled by frequency of the bigram.
 *
 * User: cstella
 * Date: 12/3/13
 * Time: 9:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScaledMutualInformationScorer implements Scorer
{
    /**
     * Score a bigram, given the context of summary statistics about the corpus of documents that it came from.
     *
     * Given a bigram of (L, R)
     * Score: P( (L,R) ) * log_2( P( L,R)/ P(L)*P(R))
     *
     * @see  http://matpalm.com/blog/2011/11/05/collocations_2/
     * @param bigram Bigram to score using mutual information
     * @param bigramStatistics Statistics about bigram frequencies across the corpus of documents
     * @param unigramStatistics Statistics about unigram frequencies across the corpus of documents
     * @return
     */
    public double score(Bigram<String> bigram, Statistics<Bigram<String>> bigramStatistics, Statistics<String> unigramStatistics)
    {
        // Scaling factor : P( (L,R) )
        double bigramFreq = bigramStatistics.getFrequency(bigram);

        // log_2( P(L, R) / (P(L) * P(R)) )
        double mutualInformation = log_2(
                bigramFreq / (unigramStatistics.getFrequency(bigram.getLeft()) * unigramStatistics.getFrequency(bigram.getRight()))
        );
        return mutualInformation*bigramFreq;
    }

    private static double log_2(double x)
    {
        return Math.log(x)/Math.log(2);
    }
}
