package com.hortonworks.pig;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.apache.pig.pigunit.PigTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cstella
 * Date: 12/5/13
 * Time: 9:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatisticallyImprobablePhrasesTest
{
    private static String[] getInputList(final String diagCode) throws IOException
    {
        final List<String> inputList = new ArrayList<String>();
        Files.readLines(new File("src/main/data/sentences.dat")
                       , Charset.defaultCharset()
                       , new LineProcessor<String>()
                        {
                            public boolean processLine(String line) throws IOException
                            {
                                String code = Iterables.getFirst(Splitter.on('\u0001').split(line), "");
                                if(code.equals(diagCode))
                                {
                                    String tuple = Joiner.on('\t').join(Splitter.on('\u0001').split(line));
                                    inputList.add(tuple);
                                }
                                return true;
                            }

                            public String getResult() {
                                return null;
                            }
                        }
                       );
        String[] output = new String[inputList.size()];
        int i = 0;
        for(String s : inputList)
        {
            output[i++] = s;
        }
        return output;
    }



    @Test
    public void testSIP() throws Exception
    {
        String[] sentences = getInputList("486");
        Assert.assertEquals(225, sentences.length );
        for(String sentence : sentences)
        {
            Assert.assertNotNull(sentence);
            Assert.assertTrue(sentence.length() > 0);
        }
        /*
         * Let's set some properties to ensure that we don't blow up with OOMs..because, you know, dealing with a few K should take 1G of heap...not.
         */
        System.getProperties().setProperty("mapred.map.child.java.opts", "-Xmx1G");
        System.getProperties().setProperty("mapred.reduce.child.java.opts","-Xmx1G");
        System.getProperties().setProperty("io.sort.mb","10");
        PigTest test = new PigTest("src/main/pig/statistically_improbable_phrases.pig", new String[] {"input=dummy", "output=dummy"});
        String[] expectedOutput = new String[] { "(486,{(year,female,0.5695993813109901),(fever,day,0.48985790465932433),(chest,pain,0.39584996114361276),(cough,fever,0.2917375920691798),(rule,pneumonia,0.2634642932021119),(female,cough,0.2358395832131406),(male,fever,0.2216812574363151),(cough,chest,0.2140317793254309),(female,followup,0.2083687960345899),(year,month,0.171335775993572)})"

        };
        test.assertOutput("SENTENCES", sentences, "SIP", expectedOutput);
    }

}
