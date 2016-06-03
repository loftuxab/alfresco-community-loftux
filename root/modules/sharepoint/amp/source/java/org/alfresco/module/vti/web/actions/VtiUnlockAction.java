package org.alfresco.module.vti.web.actions;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.UnlockMethod;
import org.alfresco.repo.webdav.WebDAVMethod;

/**
 * Executes the WebDAV LOCK method with VTI specific
 * 
 * @author DmitryVas
 */
public class VtiUnlockAction extends VtiWebDavAction implements VtiAction
{
    @Override
    public WebDAVMethod getWebDAVMethod()
    { 
        return new UnlockMethod(pathHelper);
    }
}
