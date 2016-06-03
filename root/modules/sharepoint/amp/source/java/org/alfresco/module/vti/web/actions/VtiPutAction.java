package org.alfresco.module.vti.web.actions;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.PutMethod;
import org.alfresco.repo.webdav.WebDAVMethod;

/**
 * WebDAV PUT method.
 * 
 * @author Matt Ward
 */
public class VtiPutAction extends VtiWebDavAction implements VtiAction
{

    @Override
    public WebDAVMethod getWebDAVMethod()
    {
        return new PutMethod(pathHelper);
    }

}
