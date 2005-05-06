/*
 * Created on 06-Apr-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.query;

import java.io.IOException;

public class SelfAxisStructuredFieldPosition extends AbstractStructuredFieldPosition
{

    public SelfAxisStructuredFieldPosition()
    {
        super(null, true, false);
    }

    public int matches(int start, int end, int offset) throws IOException
    {
        return offset;
    }

    public String getDescription()
    {
        return "Self Axis";
    }

    public boolean linkSelf()
    {
        return true;
    }

    public boolean isTerminal()
    {
        return false;
    }

   
    
    
}
