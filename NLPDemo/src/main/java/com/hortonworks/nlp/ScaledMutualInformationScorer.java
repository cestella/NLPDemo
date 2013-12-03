package com.hortonworks.nlp;

/**
 * Created with IntelliJ IDEA.
 * User: cstella
 * Date: 12/3/13
 * Time: 9:39 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScaledMutualInformationScorer implements Scorer {
    public double score(Bigram<String> bigram, Statistics<Bigram<String>> bigramStatistics, Statistics<String> unigramStatistics) {
        double bigramFreq = bigramStatistics.getFrequency(bigram);
        double mutualInformation = Math.log(
                bigramFreq / (unigramStatistics.getFrequency(bigram.getLeft()) * unigramStatistics.getFrequency(bigram.getRight()))
        );
        return mutualInformation*bigramFreq;
    }
}
