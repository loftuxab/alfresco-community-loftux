<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

var wikipage = String(json.get("wikipage"));
var siteId = String(json.get("siteId"));

var c = sitedata.getComponent(url.templateArgs.componentId);
c.properties["wikipage"] = wikipage;
c.save();

var uri = "/slingshot/wiki/page/" + siteId + "/" + wikipage + "?format=mediawiki";

var connector = remote.connect("alfresco");
var result = connector.get(uri);
if (result.status == status.STATUS_OK)
{
   model.pagecontent = result.response;
}
else
{
   model.pagecontent = "Error";
}

