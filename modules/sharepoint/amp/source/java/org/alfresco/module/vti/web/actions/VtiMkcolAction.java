package org.alfresco.module.vti.web.actions;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.MkcolMethod;
import org.alfresco.repo.webdav.WebDAVMethod;

/**
 * Executes the WebDAV MKCOL method with VTI specific behaviour
 */
public class VtiMkcolAction extends VtiWebDavAction implements VtiAction
{
    @Override
    public WebDAVMethod getWebDAVMethod()
    {
        return new MkcolMethod(pathHelper);
    }
}
