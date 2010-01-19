function main()
{
   var scriptRoot = new XML(config.script),
      scopedRoot = config.scoped["DocumentLibrary"]["types"];

   return (
   {
      selectable: getConfigTypes(scriptRoot, scopedRoot, args.currentType || "")
   });
}

function getConfigTypes(scriptRoot, scopedRoot, currentType)
{
   var types = [],
      configs, typeConfig, typeName;

   // Try scoped config (override defaults)
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
         return types;
      }
   }
   catch (e)
   {
   }

   // Fallback to local script config
   var xmlType = scriptRoot..type.(@name == currentType);
   if (xmlType.@name == currentType)
   {
      for each(var xmlSubtype in xmlType.subtype)
      {
         types.push(xmlSubtype.@name.toString());
      }
   }

   return types;
}

model.types = main();