package com.hortonworks.pig;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: cstella
 * Date: 12/3/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class GET_SIP extends EvalFunc<DataBag>
{
    /**
     * This callback method must be implemented by all subclasses. This
     * is the method that will be invoked on every Tuple of a given dataset.
     * Since the dataset may be divided up in a variety of ways the programmer
     * should not make assumptions about state that is maintained between
     * invocations of this method.
     *
     * @param input the Tuple to be processed.
     * @return result, of type T.
     * @throws java.io.IOException
     */
    @Override
    public DataBag exec(Tuple input) throws IOException {
        /*
         * Fill me in
         */
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
