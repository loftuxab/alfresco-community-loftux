package org.alfresco.module.vti.web.actions;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.DeleteMethod;
import org.alfresco.repo.webdav.WebDAVMethod;

/**
 * Executes the WebDAV DELETE method with VTI specific
 * 
 * @author Pavel Yurkevich
 */
public class VtiDeleteAction extends VtiWebDavAction implements VtiAction
{
    @Override
    public WebDAVMethod getWebDAVMethod()
    {
        return new DeleteMethod(pathHelper);
    }

}
