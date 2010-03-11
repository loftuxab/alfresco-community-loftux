function main()
{
   var scopedRoot = config.scoped["DocumentLibrary"]["types"];

   return (
   {
      selectable: getConfigTypes(scopedRoot, args.currentType || "")
   });
}

function getConfigTypes(scopedRoot, currentType)
{
   var types = [],
      configs, typeConfig, typeName;

   try
   {
      configs = scopedRoot.getChildren("type");
      if (configs)
      {
         for (var i = 0; i < configs.size(); i++)
         {
            // Get type qname from each config item
            typeConfig = configs.get(i);
            typeName = typeConfig.attributes["name"];
            if (typeName == currentType)
            {
               configs = typeConfig.children;
               for (var j = 0; j < configs.size(); j++)
               {
                  typeName = configs.get(j).attributes["name"];
                  if (typeName)
                  {
                     types.push(typeName.toString());
                  }
               }
               return types;
            }
         }
      }
   }
   catch (e)
   {
   }

   return types;
}

model.types = main();