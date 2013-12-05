package com.hortonworks.nlp;

/**
 * Word container object.  Keeps track of the word, the lemmatized (i.e. stemmed) version of the word and the part of speech.
 *
 * NOTE: The part of speech is from the PennTree bank
 * User: cstella
 * Date: 12/2/13
 * Time: 11:04 PM
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

    /**
     * The raw word
     * @return
     */
    public String getWord() { return word;}

    /**
     * The lemmatized version of the word.
     * @return
     */
    public String getLemma() { return lemma;}

    /**
     * The part of speech for the word.  This POS is taken from the PennTree bank.
     * @see http://www.ling.upenn.edu/courses/Fall_2003/ling001/penn_treebank_pos.html
     *
     * @return
     */
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
