package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.DwsServiceHandler;
import org.alfresco.repo.SessionUser;
import org.alfresco.repo.webdav.auth.SharepointConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

/**
 * Class for handling DeleteDws soap method
 * 
 * @author PavelYur
 *
 */
public class DeleteDwsEndpoint extends AbstractEndpoint
{
	
	private final static Log logger = LogFactory.getLog(DeleteDwsEndpoint.class);

    // handler that provides methods for operating with documents and folders
    private DwsServiceHandler handler;

    /**
     * constructor
     *
     * @param handler
     */
    public DeleteDwsEndpoint(DwsServiceHandler handler)
    {
        this.handler = handler;
    }

    /**
     * Deletes document workspace
     * 
     * @param soapRequest Vti soap request ({@link VtiSoapRequest})
     * @param soapResponse Vti soap response ({@link VtiSoapResponse}) 
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception   {
    	if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is started.");
    	}
        
        handler.deleteDws(getDwsFromUri(soapRequest), (SessionUser) soapRequest.getSession().getAttribute(SharepointConstants.USER_SESSION_ATTRIBUTE));      
        
        // creating soap response
        Element resultTag = buildResultTag(soapResponse);
        resultTag.setText(processTag("Result", "").toString());
        
        if (logger.isDebugEnabled()) {
    		logger.debug("SOAP method with name " + getName() + " is finished.");
    	}        
    }

}
