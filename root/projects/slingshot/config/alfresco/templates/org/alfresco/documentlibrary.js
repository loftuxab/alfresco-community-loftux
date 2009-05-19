function doclibType(containerType)
{
   var type = "";
   switch (String(containerType))
   {
      case "rma:filePlan":
         type = "dod5015-";
         break;
   }
   return type;
}

function main()
{
   // Need to know what type of node the container is
   var siteId = page.url.templateArgs.site,
      containerId = template.properties.container;
   
   // Assume regular cm:folder type
   var containerType = "cm:folder";

   var connector = remote.connect("alfresco");
   result = connector.get("/slingshot/doclib/container/" + siteId + "/" + containerId);
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      containerType = data.container.type;
   }

   var p = sitedata.getPage("site/" + siteId + "/dashboard");
   if (p != null)
   {
      pageMetadata = eval('(' + p.properties.pageMetadata + ')');
      pageMetadata = pageMetadata != null ? pageMetadata : {};
      doclibMeta = pageMetadata[page.id] || {};
      if (doclibMeta.titleId != null)
      {
         // Save the overridden page title into the request context
         context.setValue("page-titleId", doclibMeta.titleId);
      }
   }

   model.containerType = containerType;
   model.doclibType = doclibType(containerType);
}

main();