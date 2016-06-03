
package org.alfresco.module.vti.web.fp;

import java.io.IOException;
import java.util.Date;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling "checkin document"
 *
 * @author Dmitry Lazurkin
 *
 */
public class CheckinDocumentMethod extends AbstractMethod
{
    
    private static Log logger = LogFactory.getLog(CheckinDocumentMethod.class);
            
    public String getName()
    {
        return "checkin document";
    }

    /**
     * Enables the currently authenticated user to make changes to a document under source control
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
        String serviceName = request.getParameter("service_name", "");
        String documentName = request.getParameter("document_name", "");
        String comment = request.getParameter("comment", "");
        boolean keepCheckedOut = request.getParameter("keep_checked_out", false);
        Date timeCheckedout = request.getParameter("time_checked_out", (Date) null);

        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));

        DocMetaInfo docMetaInfo = null;
        try
        {
            docMetaInfo = vtiHandler.checkInDocument(serviceName, documentName, comment, keepCheckedOut, timeCheckedout, false);
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMethodException(e);
        }

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.beginList("meta_info");

        processDocMetaInfo(docMetaInfo, request, response);

        response.endList();
        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }
}
