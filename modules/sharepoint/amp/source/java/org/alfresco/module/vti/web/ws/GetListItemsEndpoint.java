package org.alfresco.module.vti.web.ws;

import java.util.Date;
import java.util.HashMap;

import org.alfresco.module.vti.handler.ListServiceHandler;
import org.alfresco.module.vti.handler.MethodHandler;
import org.alfresco.module.vti.metadata.model.DocsMetaInfo;
import org.alfresco.module.vti.metadata.model.ListInfoBean;

/**
 * Class for handling GetListItems method from lists web service
 *
 * @author PavelYur
 */
public class GetListItemsEndpoint extends AbstractListItemsEndpoint
{
    /**
     * constructor
     *
     * @param listHander ListServiceHandler
     * @param methodHandler that provides methods for operating with documents and folders
     */
    public GetListItemsEndpoint(ListServiceHandler listHander, MethodHandler methodHandler)
    {
        super(listHander, methodHandler);
    }

    /**
     * TODO Support all kinds of lists
     */
    protected DocsMetaInfo getListInfo(String siteName, ListInfoBean list, String initialUrl, Date changesSince)
    {
        return methodHandler.getListDocuments(siteName, false, false, "", initialUrl, false, false, true, true, false, false, false, false, new HashMap<String, Object>(0), false);
    }

}

