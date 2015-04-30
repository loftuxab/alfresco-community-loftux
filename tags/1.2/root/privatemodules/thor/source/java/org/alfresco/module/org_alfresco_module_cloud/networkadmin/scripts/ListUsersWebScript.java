package org.alfresco.module.org_alfresco_module_cloud.networkadmin.scripts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin;
import org.alfresco.module.org_alfresco_module_cloud.networkadmin.NetworkAdmin.NetworkAdminRunAsWork;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonService.TYPE;
import org.alfresco.module.org_alfresco_module_cloud.users.CloudPersonServiceImpl;
import org.alfresco.module.org_alfresco_module_cloud.webscripts.AbstractAccountBasedWebscript;
import org.alfresco.query.PagingResults;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.util.ModelUtil;
import org.alfresco.util.Pair;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class ListUsersWebScript extends AbstractAccountBasedWebscript
{
    private static int DEFAULT_MAX_ITEMS = 50;

    private CloudPersonServiceImpl cloudPersonService;
    private NetworkAdmin networkAdmin;

    public void setNetworkAdmin(NetworkAdmin networkAdmin)
    {
        this.networkAdmin = networkAdmin;
    }
    
    public void setCloudPersonService(CloudPersonServiceImpl cloudPersonService)
    {
        this.cloudPersonService = cloudPersonService;
    }

    private Map<String, Object> listPeople(WebScriptRequest req, Status status, Cache cache)
    {
        String filter = req.getParameter("filter");
        int skipCount = -1;
        int maxItems = -1;

        try
        {
            String skipCountParameter = req.getParameter("skipCount");
            if (skipCountParameter == null)
            {
                skipCount = 0;
            } else
            {
                skipCount = Integer.parseInt(skipCountParameter);
            }
        } catch (NumberFormatException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "skipCount must be a number: "
                    + req.getParameter("skipCount"));
        }

        try
        {
            String maxItemsParameter = req.getParameter("maxItems");
            if (maxItemsParameter == null)
            {
                maxItems = DEFAULT_MAX_ITEMS;
            } else
            {
                maxItems = Integer.parseInt(maxItemsParameter);
            }
        } catch (NumberFormatException e)
        {
            throw new WebScriptException(Status.STATUS_BAD_REQUEST, "maxItems must be a number: "
                    + req.getParameter("maxItems"));
        }

        String sortBy = req.getParameter("sortBy");

        Boolean networkAdmin = null;
        String networkAdminString = req.getParameter("networkAdmin");
        if (networkAdminString != null)
        {
            networkAdmin = Boolean.valueOf(networkAdminString);
        }

        String internalString = req.getParameter("internal");
        TYPE type = TYPE.ALL;
        if (internalString != null)
        {
            type = Boolean.parseBoolean(internalString) ? TYPE.INTERNAL : TYPE.EXTERNAL;
        }

        PagingResults<NodeRef> results = cloudPersonService.getPeople(filter, sortBy, skipCount, maxItems, type,
                networkAdmin);
        List<NodeRef> people = results.getPage();

        Map<String, Object> model = new HashMap<String, Object>();

        // Data
        model.put("peopleList", people);
        
        Pair<Integer, Integer> resultCount = results.getTotalResultCount();
        int totalItems = -1;
        if(resultCount != null)
        {
        	totalItems = resultCount.getFirst().intValue();
        }
        model.put("paging", ModelUtil.buildPaging(totalItems, maxItems, skipCount));

        return model;
    }

    @Override
    protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status, final Cache cache)
    {
        return networkAdmin.runAs(new NetworkAdminRunAsWork<Map<String, Object>>()
        {
            public Map<String, Object> doWork() throws Exception
            {
                return listPeople(req, status, cache);
            }
        });
    }
}
