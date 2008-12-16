
function main()
{
   // gather all required data
   var site = args["site"];
   var container = "links";
   
   var url = '/api/links/site/' + site + '/' + container + "?page=1&pageSize=512";
   
   var connector = remote.connect("alfresco");
   var result = connector.get(url);
   if (result.status != status.STATUS_OK)
   {
      status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, "Unable to do backend call. " +
                     "status: " + result.status + ", response: " + result.response);
      return null;
   }
   var data = eval('(' + result.response + ')');

   model.items = data.items;

   // set additional properties
   var lang = "en-us";
   model.lang = lang;
   model.site = site;
   model.container = container;
}

main();
