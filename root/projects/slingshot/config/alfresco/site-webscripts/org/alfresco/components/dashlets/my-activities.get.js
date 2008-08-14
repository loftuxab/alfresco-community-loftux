<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

var result = remote.call("/api/activities/feed/user");
if (result)
{
   model.entries = parseAtomFeed(new String(result));
}