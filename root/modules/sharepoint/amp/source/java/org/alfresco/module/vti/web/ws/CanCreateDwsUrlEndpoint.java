package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.alfresco.module.vti.handler.VtiHandlerException;
import org.alfresco.module.vti.metadata.dic.VtiError;
import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * Class for handling CanCreateDwsUrl soap method
 * 
 * @author Nick Smith
 *
 */
public class CanCreateDwsUrlEndpoint extends AbstractEndpoint
{
	private final static Log logger = LogFactory.getLog(CanCreateDwsUrlEndpoint.class);
	
    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;
    
    // xml namespace prefix
    private static String prefix = "dws";

    /**
     * @param handler that provides methods for operating with documents and folders
     */
    public CanCreateDwsUrlEndpoint(DwsServiceHandler handler)
    {
        super();
        this.handler = handler;
    }

    /**
    * Creates new document workspace with given title
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

        // getting title parameter from request
        XPath urlPath = new Dom4jXPath(buildXPath(prefix, "/CanCreateDwsUrl/url"));
        urlPath.setNamespaceContext(nc);
        Element url = (Element) urlPath.selectSingleNode(soapRequest.getDocument().getRootElement());
        
        String urlText = "";
        if(url != null)
        {
            urlText = url.getTextTrim();
            if (urlText.equals(""))
            {
                // [MS-DWSS] return a generated/unique name
                urlText = GUID.generate();
            }
        }
        if (false == handler.canCreateDwsUrl(urlText))
        {
           throw new VtiHandlerException(VtiError.NO_PERMISSIONS);
        }
        
        // creating soap response
        Element resultElement = buildResultTag(soapResponse);
        resultElement.setText(processTag("Result", urlText).toString());

        if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}        
    }

}
