package org.alfresco.module.vti.web.fp;

import java.io.IOException;


/**
 * Class for handling CreateUrlDirectory Method
 *
 * @author AndreyAk
 *
 */
public class CreateUrlDirectory extends AbstractMethod
{
    
    private static final String METHOD_NAME = "create url-directory";
    
    /**
     * Creates a folder for the current Web site, but only used for backward 
     * compatibility. Otherwise, use the create url directories method.
     * @param request Vti Frontpage request ({@link VtiFpRequest})
     * @param response Vti Frontpage response ({@link VtiFpResponse})      
     */
    @Override
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException
    {
        // Currently not supported
    }

    /**
     * @see VtiMethod#getName()
     */
    public String getName()
    {
        return METHOD_NAME;
    }

}
