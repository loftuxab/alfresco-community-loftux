<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

var wikipage = args.wikipage;
if (wikipage)
{
   var uri = "/slingshot/wiki/page/" + page.url.templateArgs.site + "/" + wikipage + "?format=mediawiki";

   var connector = remote.connect("alfresco");
   var result = connector.get(uri);
   if (result.status == status.STATUS_OK)
   {
      model.wikipage = result.response;
   }
   
   model.pageTitle = String(wikipage).replace(/_/g, " ");

   // Get all pages for the site so we can display links correctly
   model.pageList = doGetCall("/slingshot/wiki/pages/" + page.url.templateArgs.site);
}
