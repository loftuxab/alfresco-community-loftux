package org.alfresco.module.vti.web.actions;

import org.alfresco.module.vti.web.VtiAction;
import org.alfresco.module.vti.web.fp.PropPatchMethod;
import org.alfresco.repo.webdav.WebDAVMethod;

public class VtiPropPatchAction extends VtiWebDavAction implements VtiAction
{

    @Override
    public WebDAVMethod getWebDAVMethod()
    {
        return new PropPatchMethod(pathHelper);
    }

}
