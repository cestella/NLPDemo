package com.hortonworks.nlp;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

/**
 * The main utility class to do natural language processing.
 * User: cstella
 * Date: 12/2/13
 * Time: 11:03 PM
 */
public class NLPUtil
{

    /**
     *  This function takes a takes a set of sentences along with the statistics of individual words (unigrams)
     *  and pairs of words (bigrams) and creates a sorted list of unique bigrams ranked by their statistical improbability
     *  based around the Scorer passed in.
     *
     * @param sentences An iterable containing lists of bigrams representing a sentence
     * @param filter A predicate which determines whether a bigram is included or not
     * @param bigramStatistics Bigram summary statistics
     * @param unigramStatistics Unigram summary statistics
     * @param scorer A scorer which will rank bigrams based on their statistical improbablility
     * @return
     */
    public static SortedSet<Bigram<String>> getStatisticallyImprobableBigrams( Iterable<List<Bigram<Word>> > sentences
                                                                             , Predicate<Bigram<Word>> filter
                                                                             , Statistics<Bigram<String>> bigramStatistics
                                                                             , Statistics<String> unigramStatistics
                                                                             , Scorer scorer
                                                                             )
    {
        //Sort the bigrams in reverse-score order
        SortedSet<Bigram<String>> ret = new TreeSet<Bigram<String>>(
                   new Comparator<Bigram<String>>() {
                       public int compare(Bigram<String> o1, Bigram<String> o2) {
                           return -1*Double.compare(o1.getScore(), o2.getScore());
                       }
                   }
                                                                    );
        HashSet<Bigram<Word>> bigramCache = new HashSet<Bigram<Word>>();

        //for each sentence
        for(List<Bigram<Word>> sentence : sentences)
        {
            //and each bigram in each sentence that is not filtered out
            for(Bigram<Word> wordBigram : Iterables.filter(sentence, filter))
            {
                //if we haven't seen it before
                if(bigramCache.contains(wordBigram))
                {
                    continue;
                }
                else
                {
                    bigramCache.add(wordBigram);
                }
                //then score it add it to the sorted set
                String left = wordBigram.getLeft().getLemma();
                String right = wordBigram.getRight().getLemma();
                Bigram<String> bigram = new Bigram<String>(left, right, -1);
                bigram.setScore(scorer.score(bigram, bigramStatistics, unigramStatistics));
                ret.add(bigram);
            }
        }
        return ret;
    }

    /**
     * Function which returns a list of bigrams for a sentence in the form of List<Word>.
     * During which it updates the bigram statistics.  Note: the words are lemmatized.
     *
     * @param words list of words from a sentence
     * @param bigramStatistics set of bigram statistics
     * @return
     */
    public static List<Bigram<Word>> getBigramsPerSentence(List<Word> words, Statistics<Bigram<String>> bigramStatistics)
    {
        List<Bigram<Word>> ret = new ArrayList<Bigram<Word>>();
        for(int i = 1;i < words.size();++i)
        {
            Word left = words.get(i-1);
            Word right = words.get(i);
            bigramStatistics.add(new Bigram<String>(left.getLemma(), right.getLemma(), -1));
            ret.add(new Bigram<Word>(left, right, -1));
        }
        return ret;
    }

    /**
     * Take a (possibly multiple sentence) text and parse a list of words that we're interested in.  This only considers
     * Nouns and Verbs.  Also, the words are lemmatized.
     *
     * @param text
     * @param unigramStatistics
     * @return
     */
    public static List<Word> parseSentence(String text, Statistics<String> unigramStatistics)
    {
        Properties props = new Properties();
        props.put("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);

        // run all Annotators on this text
        pipeline.annotate(document);

        // these are all the sentences in this document
        // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<Word> ret = new ArrayList<Word>();
        for(CoreMap sentence: sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                // this is the text of the token
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                // this is the POS tag of the token
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // this is the lemma of the token
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class).toLowerCase();
                //only consider nouns and verbs.  These POS tags are from the PennTree bank.
                if(pos.startsWith("N") || pos.startsWith("V"))
                {
                    Word w = new Word(word, lemma, pos);
                    unigramStatistics.add(lemma);
                    ret.add(w);
                }
            }
        }
        return ret;
    }
}
