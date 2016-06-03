package org.alfresco.module.vti.web.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

/**
 * Class for handling IsClaimsMode soap method
 * 
 * @author Nick Burch
 */
public class IsClaimsModeEndpoint extends AbstractEndpoint
{
    private final static Log logger = LogFactory.getLog(IsClaimsModeEndpoint.class);
	
    public IsClaimsModeEndpoint()
    {
        super();
    }

    /**
    * Informs the client that we don't do claims mode
    * 
    * @param soapRequest Vti soap request ({@link VtiSoapRequest})
    * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
    */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception
    {
        if (logger.isDebugEnabled()) {
           logger.debug("SOAP method with name " + getName() + " is started.");
        }
    	
        
        // creating soap response
        Element root = soapResponse.getDocument().addElement("IsClaimsModeResponse", namespace);
        Element result = root.addElement("IsClaimsModeResult");

        // We don't support it
        result.addText("False");
        
        // Completed
        if (logger.isDebugEnabled()) {
           logger.debug("SOAP method with name " + getName() + " is finished.");
    	}        
    }    
}
