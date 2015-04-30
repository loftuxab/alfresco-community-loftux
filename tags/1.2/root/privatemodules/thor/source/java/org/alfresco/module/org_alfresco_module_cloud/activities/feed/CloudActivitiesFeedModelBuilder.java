package org.alfresco.module.org_alfresco_module_cloud.activities.feed;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.alfresco.repo.activities.feed.DefaultActivitiesFeedModelBuilder;
import org.alfresco.repo.domain.activities.ActivityFeedEntity;
import org.alfresco.util.JSONtoFmModel;
import org.json.JSONException;

/**
 * Builds up the model for activities feed emails, from {@link ActivityFeedEntity}s.
 * 
 * This class extends the {@link DefaultActivitiesFeedModelBuilder} by groups activites by a site and summarizing
 * activities by category.
 * 
 * <b>Note:</b> This class is not thread safe or reusable. A new instance should be created for each model.
 */
public class CloudActivitiesFeedModelBuilder extends DefaultActivitiesFeedModelBuilder
{
    private static final String KEY_TENANT_DOMAIN = "tenantDomain";

    public static final String OTHERS_CATEGORY = "others";
    
    public static final Set<String> DEFAULT_NON_SITE_ACTIVITY_TYPES = new HashSet<String>();
    
    private Set<String> nonSiteActivityTypes = DEFAULT_NON_SITE_ACTIVITY_TYPES;
    private Map<String, List<Map<String, Object>>> siteModels = new TreeMap<String, List<Map<String,Object>>>();
    private List<Map<String, Object>> otherModels = new LinkedList<Map<String,Object>>();
    private Map<String, String> siteNetwork = new HashMap<String, String>();
    
    private Map<String, Set<String>> activityTypeToCategoryMap = new HashMap<String, Set<String>>();
    private Map<String, AtomicLong> categoryCounts = new HashMap<String, AtomicLong>();
    private Set<String> otherActivityTypes = new HashSet<String>();
    
    private Date generationTime = new Date();

    /**
     * Default constructor
     * 
     * @param categories A Map describing the categories to be summarized. The key is the category, and the value is
     *                   the set of activity types which contribute to this category.
     */
    public CloudActivitiesFeedModelBuilder(Map<String, Set<String>> categories) 
    {
        if (categories != null && categories.size() > 0)
        {
            for (Entry<String, Set<String>> categoryEntry : categories.entrySet()) 
            {
                final String category = categoryEntry.getKey();
                final Set<String> categoryActivityTypes = categoryEntry.getValue();
                
                for (String activityType : categoryActivityTypes)
                {
                    Set<String> activtyTypeCategories = activityTypeToCategoryMap.get(activityType);
                    if (activtyTypeCategories == null) {
                        activtyTypeCategories = new HashSet<String>();
                        activityTypeToCategoryMap.put(activityType, activtyTypeCategories);
                    }
                    activtyTypeCategories.add(categoryEntry.getKey());
                    
                    categoryCounts.put(category, new AtomicLong(0));
                }
            }
        }
        categoryCounts.put(OTHERS_CATEGORY, new AtomicLong(0));
    }
    
    /**
     * Set the activity types that should not be grouped by site.
     * 
     * @param nonSiteActivityTypes
     */
    public void setNonSiteActivityType(Set<String> nonSiteActivityTypes)
    {
        this.nonSiteActivityTypes = nonSiteActivityTypes;
    }
    
    @Override
    public void addActivityFeedEntry(ActivityFeedEntity feedEntry) throws JSONException
    {
        if (ignore(feedEntry) == true) 
        {
            return;
        }
        
        super.addActivityFeedEntry(feedEntry);

        final Map<String, Object> model = feedEntry.getModel();

        String siteId = feedEntry.getSiteNetwork();

        String tenantDomain = getTenantDomain(feedEntry);
        if (tenantDomain != null ) 
        {
            siteNetwork.put(siteId, tenantDomain);;
        }
        
        String activityType = feedEntry.getActivityType();
        if (siteId == null ||
            siteId.isEmpty() ||
            nonSiteActivityTypes.contains(activityType)) 
        {
            otherModels.add(model);
        }
        else 
        {
            List<Map<String, Object>> siteActivities = siteModels.get(siteId);
            if (siteActivities == null) 
            {
              siteActivities = new ArrayList<Map<String, Object>>();
              siteModels.put(siteId, siteActivities);
            }
            siteActivities.add(model);
        }
        
        Set<String> categories = activityTypeToCategoryMap.get(activityType);
        
        if (categories != null) 
        {
            for (String category : categories) 
            {
                categoryCounts.get(category).incrementAndGet();
            }
        }
        else 
        {
            categoryCounts.get(OTHERS_CATEGORY).incrementAndGet();
            otherActivityTypes.add(activityType);
        }
    }

    private String getTenantDomain(ActivityFeedEntity feedEntry) throws JSONException
    {
        if (feedEntry.getActivitySummary() != null)
        {
            Map<String, Object> summary = JSONtoFmModel.convertJSONObjectToMap(feedEntry.getActivitySummary());
            if (summary.containsKey(KEY_TENANT_DOMAIN))
            {
                return (String) summary.get(KEY_TENANT_DOMAIN);
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> buildModel()
    {
        Map<String, Object> model = super.buildModel();
        
        model.put("generationTime", generationTime);
        model.put("activitiesBySite", siteModels);
        model.put("nonSiteActivities", otherModels);
        for (Entry<String, AtomicLong> categoryCountEntry : categoryCounts.entrySet())
        {
            model.put(categoryCountEntry.getKey(), categoryCountEntry.getValue().longValue());
        }
        model.put("otherActivityTypes", otherActivityTypes);
        model.put("siteNetwork", siteNetwork);
        return model;
    }
}
