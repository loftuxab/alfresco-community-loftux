package org.alfresco.module.vti.web.actions;

import org.alfresco.module.vti.handler.alfresco.UrlHelper;
import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.PropfindMethod;
import org.alfresco.repo.webdav.WebDAVMethod;

/**
* Executes the WebDAV PROPFIND method with VTI specific
*
* @author PavelYur
*
*/
public class VtiPropfindAction extends VtiWebDavAction implements VtiAction
{
    private UrlHelper urlHelper;
    
    @Override
    public WebDAVMethod getWebDAVMethod()
    {
        return new PropfindMethod(pathHelper, urlHelper);
    }

    public void setUrlHelper(UrlHelper urlHelper)
    {
        this.urlHelper = urlHelper;
    }
}


