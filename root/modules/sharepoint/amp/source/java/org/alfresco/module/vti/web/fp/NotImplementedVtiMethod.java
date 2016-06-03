
package org.alfresco.module.vti.web.fp;

import java.io.IOException;

import org.alfresco.module.vti.metadata.dic.VtiError;

public class NotImplementedVtiMethod extends AbstractMethod
{
    private String vtiMethodName;
    
    /** 
     * Method that will be called if client sends request to undefined method 
     * 
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})
     */
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException
    {
        throw new VtiMethodException(VtiError.V_OWSSVR_ERRORSERVERINCAPABLE);
    }
    
    public NotImplementedVtiMethod(String vtiMethodName)
    {
        this.vtiMethodName = vtiMethodName;
    }

    public String getName()
    {
        return vtiMethodName;
    }
    
}
