package com.hortonworks.nlp;

/**
 * Created with IntelliJ IDEA.
 * User: cstella
 * Date: 12/2/13
 * Time: 11:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bigram<T>
{
    private T left;
    private T right;
    private double score;

    Bigram(T left, T right, double score)
    {
        this.left = left;
        this.right = right;
        this.score = score;
    }

    public T getLeft() { return left;}
    public T getRight() { return right;}
    public double getScore() { return score;}

    public void setScore(double s) { score = s;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bigram bigram = (Bigram) o;

        if (left != null ? !left.equals(bigram.left) : bigram.left != null) return false;
        if (right != null ? !right.equals(bigram.right) : bigram.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }


}
