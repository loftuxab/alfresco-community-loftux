function main()
{
   var connector = remote.connect("alfresco"),
      result = connector.get("/slingshot/doclib/types/node/" + args.nodeRef.replace(":/", ""));

   var selectable = [];
   
   if (result.status == 200)
   {
      var data = eval('(' + result + ')');
      selectable = data.selectable;
   }

   model.selectable = selectable;
}

main();