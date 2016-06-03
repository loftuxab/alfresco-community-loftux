package org.alfresco.encoding;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;

/**
 * Uses Apache Tika as a fallback encoding detector
 * 
 * @since 3.4
 * @author Nick Burch
 */
public class TikaCharsetFinder extends AbstractCharactersetFinder
{
    private static Log logger = LogFactory.getLog(TikaCharsetFinder.class);
    
    private int threshold = 35;
    
    @Override
    protected Charset detectCharsetImpl(byte[] buffer) throws Exception
    {
        CharsetDetector detector = new CharsetDetector();
        detector.setText(buffer);
        CharsetMatch match = detector.detect();

        if(match != null && match.getConfidence() > threshold)
        {
            try
            {
                return Charset.forName(match.getName());
            }
            catch(UnsupportedCharsetException e)
            {
                logger.info("Charset detected as " + match.getName() + " but the JVM does not support this, detection skipped");
            }
        }
        return null;
    }

    /**
     * Return the matching threshold before we decide that
     *  what we detected is a good match. In the range
     *  0-100.
     */
    public int getThreshold()
    {
        return threshold;
    }

    /**
     * At what point do we decide our match is good enough?
     * In the range 0-100. If we don't reach the threshold,
     *  we'll decline, and either another finder will work on
     *  it or the fallback encoding will be taken.
     */
    public void setThreshold(int threshold)
    {
        if(threshold < 0)
            threshold = 0;
        if(threshold > 100)
            threshold = 100;
        
        this.threshold = threshold;
    }
    
}
