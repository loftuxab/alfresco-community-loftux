package org.alfresco.module.vti.web.actions;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.LockMethod;
import org.alfresco.repo.webdav.WebDAVMethod;

/**
 * Executes the WebDAV LOCK method with VTI specific
 * 
 * @author DmitryVas
 */
public class VtiLockAction extends VtiWebDavAction implements VtiAction
{
    @Override
    public WebDAVMethod getWebDAVMethod()
    {
        return new LockMethod(pathHelper);
    }
    
}
