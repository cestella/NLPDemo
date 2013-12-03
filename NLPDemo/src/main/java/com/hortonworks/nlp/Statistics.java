package com.hortonworks.nlp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cstella
 * Date: 12/2/13
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Statistics<T>
{
    private Map<T, Integer> counts = new HashMap<T, Integer>();
    private int total = 0;
    public Statistics()
    {

    }

    public void add(T w)
    {
        Integer cnt = counts.get(w);
        if(cnt == null) { cnt = 0; }
        counts.put(w, cnt + 1);
        total++;
    }

    public double getFrequency(T lemma)
    {
        Integer cnt = counts.get(lemma);
        if(cnt == null) { cnt = 0;}
        return 1.0*cnt/total;
    }

    public Set<T> getDomain()
    {
        return counts.keySet();
    }

}
