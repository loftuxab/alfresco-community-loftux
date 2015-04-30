<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Activities Feed GET method
 */
Admin.initModel(
   "Alfresco:Type=Configuration,Category=ActivitiesFeed,id1=default",
   ["activities.feed.notifier.enabled", "activities.feed.notifier.cronExpression", "activities.feed.max.size", "activities.feed.max.ageMins"],
   "admin-activitiesfeed"
);
