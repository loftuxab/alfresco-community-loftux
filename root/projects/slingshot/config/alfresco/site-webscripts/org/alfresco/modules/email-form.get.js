function main()
{
   var connector = remote.connect("alfresco");
   var result = connector.get("/api/sites");
   var templates = [];
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      templates.push("test-templates.ftl");
      templates.push("test-templates-2.ftl");
   }
   model.templates = templates;
}

main();
