package org.alfresco.module.vti.web.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.web.VtiAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiIfHeaderAction is used for merging between client document version
* and server document version.</p>
* 
* @author PavelYur
*/
public class VtiIfHeaderAction extends HttpServlet implements VtiAction
{

    private static final long serialVersionUID = 3119971805600532320L;

    private final static Log logger = LogFactory.getLog(VtiIfHeaderAction.class);

    private MethodHandler handler;

    /**
     * <p>
     * MethodHandler setter
     * </p>
     * @param handler {@link org.alfresco.module.vti.handler.MethodHandler}
     */
    public void setHandler(MethodHandler handler)
    {
        this.handler = handler;
    }

    /**
     * <p>Getting server version of document for merging.</p> 
     *
     * @param req HTTP request
     * @param resp HTTP response
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        this.handler.existResource(req, resp);
    }

    /**
     * <p>Saving of client version of document while merging.</p> 
     *
     * @param req HTTP request
     * @param resp HTTP response
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        this.handler.putResource(req, resp);
    }

    /**
     * <p>Merge between client document version and server document version.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            service(request, response);
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action IO exception", e);
            }
        }
        catch (ServletException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action execution exception", e);
            }
        }
    }
}
