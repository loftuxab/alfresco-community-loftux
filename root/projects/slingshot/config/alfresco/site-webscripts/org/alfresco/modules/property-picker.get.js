function main()
{
   var connector = remote.connect("alfresco");
   var result = connector.get("/api/actionConstraints/ac-content-properties");
   var templates = [];
   if (result.status == 200)
   {
      model.transientContentProperties = jsonUtils.toJSONString(eval('(' + result + ')').data.values);
   }
}

main();
