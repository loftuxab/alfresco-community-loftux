<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

var wikipage = args.wikipage;
if (wikipage)
{
   var uri = "/slingshot/wiki/page/" + page.url.templateArgs.site + "/" + encodeURIComponent(wikipage) + "?format=mediawiki",
      connector = remote.connect("alfresco"),
      result = connector.get(uri),
      myConfig = new XML(config.script);
   
   if (result.status == status.STATUS_OK)
   {
      model.wikipage = myConfig.allowUnfilteredHTML == true ? result.response : stringUtils.stripEncodeUnsafeHTML(result.response);
   }
   
   model.wikiLink = String(wikipage);
   model.pageTitle = String(wikipage).replace(/_/g, " ");

   // Get all pages for the site so we can display links correctly
   model.pageList = doGetCall("/slingshot/wiki/pages/" + page.url.templateArgs.site);
}

// Call the repository to see if the user is site manager or not
var userIsSiteManager = false;
var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + stringUtils.urlEncode(user.name));
if (json.status == 200)
{
   var obj = eval('(' + json + ')');
   if (obj.role)
   {
      userIsSiteManager = (obj.role == "SiteManager");
   }
}
model.userIsSiteManager = userIsSiteManager;