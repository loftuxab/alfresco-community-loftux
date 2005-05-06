/*
 * Created on Mar 14, 2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.search.impl.lucene.query;

import java.io.IOException;

/**
 * This class patches a term at a specified location.
 * 
 * @author andyh
 */
public class AbsoluteStructuredFieldPosition extends AbstractStructuredFieldPosition
{

    int requiredPosition;

    /**
     * Search for a term at the specified position.
     */

    public AbsoluteStructuredFieldPosition(String termText, int position)
    {
        super(termText, true, true);
        this.requiredPosition = position;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.lucene.extensions.StructuredFieldPosition#matches(int,
     *      org.apache.lucene.index.TermPositions)
     */
    public int matches(int start, int end, int offset) throws IOException
    {
        if (offset >= requiredPosition)
        {
            return -1;
        }

        if (getCachingTermPositions() != null)
        {
            // Doing "termText"
            getCachingTermPositions().reset();
            int count = getCachingTermPositions().freq();
            int realPosition = 0;
            int adjustedPosition = 0;
            for (int i = 0; i < count; i++)
            {
                realPosition = getCachingTermPositions().nextPosition();
                adjustedPosition = realPosition - start;
                if ((end != -1) && (realPosition > end))
                {
                    return -1;
                }
                if (adjustedPosition > requiredPosition)
                {
                    return -1;
                }
                if (adjustedPosition == requiredPosition)
                {
                    return adjustedPosition;
                }

            }
        }
        else
        {
            // Doing "*"
            if ((offset + 1) == requiredPosition)
            {
                return offset + 1;
            }
        }
        return -1;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.lucene.extensions.StructuredFieldPosition#getPosition()
     */
    public int getPosition()
    {
        return requiredPosition;
    }

    public String getDescription()
    {
        return "Absolute Named child";
    }

 
    
}