package org.alfresco.repo.content.encoding;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import org.alfresco.encoding.CharactersetFinder;
import org.alfresco.service.cmr.repository.MimetypeService;

/**
 * Utility bean to guess the charset given a stream and a mimetype.
 * 
 * @since 2.1
 * @author Derek Hulley
 */
public class ContentCharsetFinder
{
    private Charset defaultCharset = Charset.defaultCharset();
    private MimetypeService mimetypeService;
    private List<CharactersetFinder> charactersetFinders;

    /**
     * Override the system default charset.  Where the characterset cannot be determined for
     * a mimetype and input stream, this mimetype will be used.  The default is 'UTF-8'.
     * 
     * @param defaultCharset            the default characterset
     */
    public void setDefaultCharset(String defaultCharset)
    {
        this.defaultCharset = Charset.forName(defaultCharset);
    }
    
    /**
     * Set the mimetype service that will help determine if a particular mimetype can be
     * treated as encoded text or not.
     */
    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }
    
    /**
     * Set the list of characterset finder to execute, in order, for text based content.
     * @param charactersetFinders       a list of finders
     */
    public void setCharactersetFinders(List<CharactersetFinder> charactersetFinders)
    {
        this.charactersetFinders = charactersetFinders;
    }
    
    /**
     * Gets the characterset from the stream, if the mimetype is text and the text
     * has enough information to give the encoding away.  Otherwise, the default
     * is returned.
     * 
     * @param is                a stream that will not be affected by the call, but must
     *                          support marking
     * @param mimetype          the mimetype of the stream data - <tt>null</tt> if not known
     * @return                  returns a characterset and never <tt>null</tt>
     */
    public Charset getCharset(InputStream is, String mimetype)
    {
        if (mimetype == null)
        {
            return defaultCharset;
        }
        // Is it text?
        if (!mimetypeService.isText(mimetype))
        {
            return defaultCharset;
        }
        // Try the finders
        Charset charset = null;
        for (CharactersetFinder finder : charactersetFinders)
        {
            charset = finder.detectCharset(is);
            if (charset != null)
            {
                break;
            }
        }
        // Done
        if (charset == null)
        {
            return defaultCharset;
        }
        else
        {
            return charset;
        }
    }
}
