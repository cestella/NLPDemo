package com.hortonworks.demo;

import com.google.common.base.Joiner;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static char SEPARATOR = '\u0001';
    public static void main( String[] args ) throws JDOMException, IOException {
        File file = new File(args[0]);
        File outFile = new File(args[1]);
        PrintWriter pw = new PrintWriter(new FileWriter(outFile));
        SAXBuilder builder = new SAXBuilder();
        Document doc = (Document)builder.build(file);
        for(Element e : doc.getRootElement().getChildren("doc"))
        {
            List<String> codes = new ArrayList<String>();
            for(Element codeElem : e.getChild("codes").getChildren("code"))
            {
                if(codeElem.getAttributeValue("origin").equals("CMC_MAJORITY"))
                {
                    codes.add(codeElem.getValue());
                }
            }
            List<String> sentences = new ArrayList<String>();
            for(Element textElem : e.getChild("texts").getChildren("text"))
            {
                String sentence = textElem.getValue();
                String type = textElem.getAttributeValue("type");
                for(String code : codes)
                {
                    pw.println(Joiner.on(SEPARATOR).join(code, type, sentence));
                }
            }
            pw.flush();
        }

        pw.close();

    }
}
