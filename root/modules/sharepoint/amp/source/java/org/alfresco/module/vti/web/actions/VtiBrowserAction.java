package org.alfresco.module.vti.web.actions;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.alfresco.module.vti.web.VtiAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiBrowserAction is used for redirection of specific requests to Web clients.
* It is used by browser while it is opening by client.</p>
*
* @author PavelYur
*
*/
public class VtiBrowserAction implements VtiAction
{

    private DwsServiceHandler handler;

    private static final long serialVersionUID = 5032228836777952601L;

    private static Log logger = LogFactory.getLog(VtiBrowserAction.class);

    /**
     * <p>VtiHandler setter.</p>
     *
     * @param handler {@link DwsServiceHandler}.    
     */
    public void setHandler(DwsServiceHandler handler)
    {
        this.handler = handler;
    }
    
    /**
     * <p>Handle redirection of specific requests to Web clients.</p> 
     *
     * @param req HTTP request
     * @param resp HTTP response
     */
    public void execute(HttpServletRequest req, HttpServletResponse resp)
    {
        if (logger.isDebugEnabled())
            logger.debug("Handle request to browser '" + req.getRequestURI() + "'");
        try
        {
            handler.handleRedirect(req, resp);
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled()) {
                logger.debug("Action execution exception", e);
            }
        }
    }
}
