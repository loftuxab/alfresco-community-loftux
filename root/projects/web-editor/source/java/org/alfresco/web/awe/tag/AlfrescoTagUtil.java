package org.alfresco.web.awe.tag;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;

/**
 * Tag utilities for Alfresco Web Editor
 * 
 * @author muzquiano
 */
public class AlfrescoTagUtil 
{
    public static final String KEY_MARKER_ID_PREFIX = "awe_marker_id_prefix";
    public static final String KEY_EDITABLE_CONTENT = "awe_editable_content";	
	
    /**
     * Returns the list of marked content that has been discovered.
     * <p>
     * This list is built up as each markContent tag is encountered.
     * </p>
     * 
     * @return List of MarkedContent objects
     */
    @SuppressWarnings("unchecked")
    public static List<MarkedContent> getMarkedContent(ServletRequest request)
    {
        List<MarkedContent> markedContent = (List<MarkedContent>) request.getAttribute(KEY_EDITABLE_CONTENT);

        if (markedContent == null)
        {
            markedContent = new ArrayList<MarkedContent>();
            request.setAttribute(KEY_EDITABLE_CONTENT, markedContent);
        }

        return markedContent;
    }
}
