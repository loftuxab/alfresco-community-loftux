package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling RemoveDwsUser soap method
 * 
 * @author PavelYur
 *
 */
public class RemoveDwsUserEndpoint extends AbstractEndpoint
{
	
	private final static Log logger = LogFactory.getLog(RemoveDwsUserEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * constructor
     *
     * @param handler
     */
    public RemoveDwsUserEndpoint(DwsServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Removes user from site
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
    	if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is started.");
    	}    	
        // mapping xml namespace to prefix
        SimpleNamespaceContext nc = new SimpleNamespaceContext();
        nc.addNamespace(prefix, namespace);
        nc.addNamespace(soapUriPrefix, soapUri);

        // getting id parameter from request
        XPath idPath = new Dom4jXPath(buildXPath(prefix, "/RemoveDwsUser/id"));
        idPath.setNamespaceContext(nc);
        Element id = (Element) idPath.selectSingleNode(soapRequest.getDocument().getRootElement());
        
        handler.removeDwsUser(getDwsFromUri(soapRequest), id.getText());
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("RemoveDwsUserResponse", namespace);
        Element removeDwsUserResult = root.addElement("RemoveDwsUserResult");

        removeDwsUserResult.setText("<Results/>");
        
        if (logger.isDebugEnabled()) 
        {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}
        
    }

}
