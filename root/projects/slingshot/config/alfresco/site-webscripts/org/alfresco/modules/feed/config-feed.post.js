<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

var uri = String(json.get("url"));
if (uri !== "")
{
   var re = /^http:\/\//;
   if (!re.test(uri))
   {
      uri = "http://" + uri;
   }
   
   var c = sitedata.getComponent(url.templateArgs.componentId);
   c.properties["feedurl"] = uri;
   c.save();
   
   model.items = getRSSFeed(uri);
}

