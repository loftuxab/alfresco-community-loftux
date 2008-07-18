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
}
