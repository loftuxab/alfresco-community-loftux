package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.alfresco.service.cmr.model.FileNotFoundException;

/**
 * Class for handling the DeleteList soap method
 * 
 * @author Nick Burch
 */
public class DeleteListEndpoint extends AbstractListEndpoint
{
    /**
     * constructor
     *
     * @param handler
     */
    public DeleteListEndpoint(ListServiceHandler handler)
    {
        super(handler);
    }

    /**
     * Fetch and delete the list
     */
    @Override
    protected ListInfoBean executeListAction(VtiSoapRequest soapRequest, String dws, String listName,
            String description, int templateID) throws Exception
    {
        // Try to delete it
        try
        {
           handler.deleteList(listName, dws);
        }
        catch(SiteDoesNotExistException se)
        {
           // The specification defines the exact code that must be
           //  returned in case of a file not being found
           long code = VtiError.V_LIST_NOT_FOUND.getErrorCode();
           String message = "Site not found: " + se.getMessage();
           throw new VtiSoapException(message, code, se);
        }
        catch(FileNotFoundException fnfe)
        {
           // The specification defines the exact code that must be
           //  returned in case of a file not being found
           long code = VtiError.V_LIST_NOT_FOUND.getErrorCode();
           String message = "List not found: " + fnfe.getMessage();
           throw new VtiSoapException(message, code, fnfe);
        }
        
        // List has now gone
        return null;
    }

    /**
     * The Delete response is very simple
     */
    @Override
    protected void renderList(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse, String siteName,
            ListInfoBean list) throws Exception
    {
       // If we managed to delete the list, we simply send an empty <DeleteListResponse />
       soapResponse.getDocument().addElement("DeleteListResponse");
    }
}