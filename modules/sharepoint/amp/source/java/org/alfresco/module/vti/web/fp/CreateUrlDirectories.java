package org.alfresco.module.vti.web.fp;

import java.io.IOException;

import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.module.vti.metadata.model.DocMetaInfo;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for handling CreateUrlDirectories Method
 * 
 * @author AndreyAk
 *
 */
public class CreateUrlDirectories extends AbstractMethod
{
    private static Log logger = LogFactory.getLog(CreateUrlDirectories.class);
    
    private static final String METHOD_NAME = "create url-directories";
    
    /** 
     * Allows the client to create one or more directories (folders) on the Web site. 
     * This operation is not atomic. If the bulk operation fails, some of the earlier 
     * directories might have been created. In the case of a failure, the client SHOULD 
     * query the server with list documents or if it needs to determine what folders were 
     * created.Note Clients SHOULD use this method rather than create url-directory.
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
        DocsMetaInfo urldirs = request.getParameter("urldirs", new DocsMetaInfo());

        serviceName = VtiPathHelper.removeSlashes(serviceName.replaceFirst(request.getAlfrescoContextName(), ""));
        try
        {
            for (DocMetaInfo dir : urldirs.getFolderMetaInfoList())
            {
                vtiHandler.createDirectory(serviceName, dir);
            }
        }
        catch (VtiHandlerException e)
        {
            throw new VtiMethodException(e);
        }

        response.beginVtiAnswer(getName(), ServerVersionMethod.version);
        response.addParameter("message=successfully created URL-directories");
        response.endVtiAnswer();

        if (logger.isDebugEnabled())
        {
            logger.debug("End of method execution. Method name: " + getName());
        }
    }

    /**
     * @see VtiMethod#getName()
     */
    public String getName()
    {
        return METHOD_NAME;
    }

}
