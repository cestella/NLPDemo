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
 * Created with IntelliJ IDEA.
 * User: cstella
 * Date: 12/2/13
 * Time: 11:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class NLPUtil
{

    public static SortedSet<Bigram<String>> getStatisticallyImprobableBigrams( Iterable<List<Bigram<Word>> > sentences
                                                                             , Predicate<Bigram<Word>> filter
                                                                             , Statistics<Bigram<String>> bigramStatistics
                                                                             , Statistics<String> unigramStatistics
                                                                             , Scorer scorer
                                                                             )
    {
        SortedSet<Bigram<String>> ret = new TreeSet<Bigram<String>>(
                   new Comparator<Bigram<String>>() {
                       public int compare(Bigram<String> o1, Bigram<String> o2) {
                           return -1*Double.compare(o1.getScore(), o2.getScore());
                       }
                   }
                                                                    );
        HashSet<Bigram<Word>> bigramCache = new HashSet<Bigram<Word>>();
        for(List<Bigram<Word>> sentence : sentences)
        {
            for(Bigram<Word> wordBigram : Iterables.filter(sentence, filter))
            {
                if(bigramCache.contains(wordBigram))
                {
                    continue;
                }
                else
                {
                    bigramCache.add(wordBigram);
                }
                String left = wordBigram.getLeft().getLemma();
                String right = wordBigram.getRight().getLemma();
                Bigram<String> bigram = new Bigram<String>(left, right, -1);
                bigram.setScore(scorer.score(bigram, bigramStatistics, unigramStatistics));
                ret.add(bigram);
            }
        }
        return ret;
    }

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
