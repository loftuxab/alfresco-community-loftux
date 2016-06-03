
package org.alfresco.module.vti.web.fp;


/**
 * Interface that must implement all the Vti methods (Frontpage extension protocol methods)
 * handling classes
 * 
 * @author Michael Shavnev
 */
public interface VtiMethod
{
    /**
     * @return the name of the vti method
     */
    public String getName();

    /**
     * Executes the vti method
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     */
    public void execute(VtiFpRequest request, VtiFpResponse response);

}
