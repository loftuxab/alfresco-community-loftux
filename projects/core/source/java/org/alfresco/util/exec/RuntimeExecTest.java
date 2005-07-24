package org.alfresco.util.exec;

import junit.framework.TestCase;

/**
 * @see org.alfresco.util.exec.RuntimeExec
 * 
 * @author Derek Hulley
 */
public class RuntimeExecTest extends TestCase
{
    /**
     * execute a statement and catch the output
     */
    public void testStreams() throws Exception
    {
        RuntimeExec exec = new RuntimeExec("find");
        int retCode = exec.execute();
        
        String out = exec.getStdOut();
        String err = exec.getStdErr();
        
        assertTrue("Didn't expect any non-error output", out.length() == 0);
        assertTrue("No error output found", err.length() > 0);
    }
}
