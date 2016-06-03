
package org.alfresco.module.vti.web.ws;

import org.alfresco.module.vti.handler.alfresco.UrlHelper;


/**
* Interface that must implement all the Vti endpoints realizations
*   
* @author Stas Sokolovsky
*
*/
public interface VtiEndpoint
{
    /**
     * Executes target endpoint method
     * 
     * @param soapRequest Vti Soap Request ({@link VtiSoapRequest})
     * @param soapResponse Vti Soap Response ({@link VtiSoapResponse})
     */
    public void execute(VtiSoapRequest soapRequest, VtiSoapResponse soapResponse) throws Exception;
    
    /**
     * @return the name of the endpoint
     */
    public String getName();
    
    /**
     * @return the namespace of the endpoint
     */
    public String getNamespace();
    
    public UrlHelper getUrlHelper();

    public String getResponseTagName();

    public String getResultTagName();
}
