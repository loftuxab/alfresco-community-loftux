package org.alfresco.module.vti.web.fp;

import java.io.IOException;


/**
 * @author PavelYur
 *
 */
public class SaveFormDialog extends AbstractMethod
{

    private static final String METHOD_NAME = "dialogview";
    
    /**
     * @see AbstractMethod#doExecute(VtiFpRequest, VtiFpResponse)
     */
    @Override
    protected void doExecute(VtiFpRequest request, VtiFpResponse response) throws VtiMethodException, IOException
    {
        // do nothing
    }

    /**
     * @see VtiMethod#getName()
     */
    public String getName()
    {
        return METHOD_NAME;
    }

}
