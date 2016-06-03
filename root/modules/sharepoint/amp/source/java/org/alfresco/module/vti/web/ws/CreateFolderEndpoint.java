package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling CreateFolder soap method
 * 
 * @author PavelYur
 *
 */
public class CreateFolderEndpoint extends AbstractEndpoint
{
	
	private final static Log logger = LogFactory.getLog(CreateFolderEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;

    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * constructor
     *
     * @param handler
     */
    public CreateFolderEndpoint(DwsServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Creates new folder in site's document library
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

        // getting url parameter from request
        XPath urlPath = new Dom4jXPath(buildXPath(prefix, "/CreateFolder/url"));
        urlPath.setNamespaceContext(nc);
        Element url = (Element) urlPath.selectSingleNode(soapRequest.getDocument().getRootElement());
        
        handler.createFolder(getDwsFromUri(soapRequest) + "/" + url.getText());
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("CreateFolderResponse", namespace);
        Element createFolderResult = root.addElement("CreateFolderResult");

        // Contents are standalone XML
        createFolderResult.setText("<Result/>");
        
        if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}
        
    }

}
