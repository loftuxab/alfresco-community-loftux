
package org.alfresco.module.vti.web.actions;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.module.vti.web.VtiAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
* <p>VtiInfAction returns the information to determine the entry point for
* the Microsoft FrontPage Server Extensions.</p>
*
* @author Michael Shavnev
*/
public class VtiInfAction implements VtiAction
{
    private static final long serialVersionUID = 429709350002602411L;

    private final static Log logger = LogFactory.getLog(VtiBaseAction.class);

    /**
     * <p>Return the information to determine the entry point for 
     * the Microsoft FrontPage Server Extensions.</p> 
     *
     * @param request HTTP request
     * @param response HTTP response
     */
    public void execute(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write("<!-- FrontPage Configuration Information\n".getBytes());
            outputStream.write(" FPVersion=\"14.00.0.000\"\n".getBytes());
            outputStream.write("FPShtmlScriptUrl=\"_vti_bin/shtml.dll/_vti_rpc\"\n".getBytes());
            outputStream.write("FPAuthorScriptUrl=\"_vti_bin/_vti_aut/author.dll\"\n".getBytes());
            outputStream.write("FPAdminScriptUrl=\"_vti_bin/_vti_adm/admin.dll\"\n".getBytes());
            outputStream.write("TPScriptUrl=\"_vti_bin/owssvr.dll\"\n".getBytes());
            outputStream.write("-->".getBytes());
            outputStream.close();
        }
        catch (IOException e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Action IO exception", e);
            }
        }
    }

}