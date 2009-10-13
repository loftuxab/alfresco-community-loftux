<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils/feed.utils.js">

var c = sitedata.getComponent(url.templateArgs.componentId);

var uri = String(json.get("url"));
if (uri !== "")
{
   var re = /^http:\/\//;
   if (!re.test(uri))
   {
      uri = "http://" + uri;
   }
   
   c.properties["feedurl"] = uri;

   var feed = getRSSFeed(uri);
   model.title = feed.title;
   model.items = feed.items;
}

var target;
if (json.isNull("new_window"))
{
   // Doesn't seem to like setting properties as boolean so we use a string instead
   target = "_self";
}
else
{
   target = "_blank";
}
model.target = target;
c.properties["target"] = target;

var limit = String(json.get("limit"));
if (limit === "all")
{
   c.properties["limit"] = null; // reset
   model.limit = 999;
}
else
{
   c.properties["limit"] = limit;
   model.limit = limit;
}

c.save();
