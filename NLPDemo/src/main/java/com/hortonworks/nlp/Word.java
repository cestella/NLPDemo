package com.hortonworks.nlp;

/**
 * Created with IntelliJ IDEA.
 * User: cstella
 * Date: 12/2/13
 * Time: 11:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Word
{
    private String word;
    private String lemma;
    private String partOfSpeech;
    Word(String word, String lemma, String partOfSpeech)
    {
        this.word = word;
        this.lemma = lemma;
        this.partOfSpeech = partOfSpeech;
    }

    public String getWord() { return word;}
    public String getLemma() { return lemma;}
    public String getPartOfSpeech() { return partOfSpeech;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word1 = (Word) o;

        if (lemma != null ? !lemma.equals(word1.lemma) : word1.lemma != null) return false;
        if (partOfSpeech != null ? !partOfSpeech.equals(word1.partOfSpeech) : word1.partOfSpeech != null) return false;
        if (word != null ? !word.equals(word1.word) : word1.word != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = word != null ? word.hashCode() : 0;
        result = 31 * result + (lemma != null ? lemma.hashCode() : 0);
        result = 31 * result + (partOfSpeech != null ? partOfSpeech.hashCode() : 0);
        return result;
    }
}
