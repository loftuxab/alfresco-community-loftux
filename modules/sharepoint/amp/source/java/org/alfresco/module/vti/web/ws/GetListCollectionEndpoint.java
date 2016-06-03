package org.alfresco.module.vti.web.ws;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.model.ListInfoBean;
import org.alfresco.repo.site.SiteDoesNotExistException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;

/**
 * Class for handling GetListCollection method from lists web service
 *
 * @author PavelYur
 */
public class GetListCollectionEndpoint extends AbstractListEndpoint
{
    private static Log logger = LogFactory.getLog(GetWebCollectionEndpoint.class);

    // xml namespace prefix
    private static String prefix = "lists";
    
    /**
     * constructor
     *
     * @param handler that provides methods for operating with documents and folders
     */
    public GetListCollectionEndpoint(ListServiceHandler handler)
    {
        super(handler);
    }
    
    /**
     * Returns the names and GUIDs for all the lists in the site
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("Soap Method with name " + getName() + " is started.");

        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        // get site name that is used to list subsites
        String siteName = getDwsFromUri(soapRequest);
        
        List<ListInfoBean> lists;
        
        if (siteName.equals(""))
        {
            lists = new ArrayList<ListInfoBean>(0); 
        }
        else
        {
            try
            {
                lists = handler.getListCollection(siteName);
            }
            catch (SiteDoesNotExistException e)
            {
                throw new VtiHandlerException(VtiHandlerException.BAD_URL);
            }
        }

        // creating soap response
        Element responseElement = soapResponse.getDocument().addElement("GetListCollectionResponse", namespace);
        Element resultElement = responseElement.addElement("GetListCollectionResult");       
        Element listsElement = resultElement.addElement("Lists");
        
        for (ListInfoBean list : lists)
        {
            String siteUrl = getContext(soapRequest) + "/" + siteName;
            
            Element listElement = listsElement.addElement("List");
            renderListDefinition(list, siteName, siteUrl, listElement);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Soap Method with name " + getName() + " is finished.");
        }
    }

    @Override
    protected ListInfoBean executeListAction(VtiSoapRequest soapRequest, String dws, String listName,
            String description, int templateID) throws Exception
    {
        throw new IllegalStateException("Should not be called, GetListCollections has special handling");
    }
}
