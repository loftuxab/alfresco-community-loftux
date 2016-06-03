package org.alfresco.module.vti.web.fp;

import java.io.IOException;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.web.VtiEncodingUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling "url to web url" method
 * 
 * @author PavelYur
 */
public class UrlToWebUrlMethod extends AbstractMethod
{
    private static Log logger = LogFactory.getLog(UrlToWebUrlMethod.class);
    
    /**
     * Given a URL for a file, returns the URL of the Web site to which 
     * the file belongs, and the subsite, if applicable
     * 
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})
     */
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Start method execution. Method name: " + getName());
        }
        String url = request.getParameter("url", "");

        if (url != null && url.length() > 0)
        {
            String alfrescoContext = request.getAlfrescoContextName();
            String[] relativeUrls = null;
            try
            {
                relativeUrls = vtiHandler.decomposeURL(url, alfrescoContext);
            }
            catch (VtiHandlerException e)
            {
                throw new VtiMethodException(e);
            }

            response.beginVtiAnswer(getName(), ServerVersionMethod.version);
            response.addParameter("webUrl=" + VtiEncodingUtils.encode(relativeUrls[0]));
            response.addParameter("fileUrl=" + VtiEncodingUtils.encode(relativeUrls[1]));
            response.endVtiAnswer();
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

    /**
     * returns methods name
     */
    public String getName()
    {
        return "url to web url";
    }

}
