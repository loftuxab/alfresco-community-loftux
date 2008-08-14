<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

var result = remote.call("/api/activities/feed/site/" + page.url.templateArgs.site + "?format=atomfeed");
if (result)
{  
   model.entries = parseAtomFeed(new String(result));
}

