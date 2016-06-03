package org.alfresco.module.vti.web.actions;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.MoveMethod;
import org.alfresco.repo.webdav.WebDAVMethod;

/**
 * Executes the WebDAV MOVE method with VTI specific behaviour
 */
public class VtiMoveAction extends VtiWebDavAction implements VtiAction
{
    @Override
    public WebDAVMethod getWebDAVMethod()
    {
        return new MoveMethod(pathHelper);
    }
}
