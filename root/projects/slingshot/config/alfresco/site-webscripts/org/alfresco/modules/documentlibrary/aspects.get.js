function main()
{
   var scriptRoot = new XML(config.script),
      scopedRoot = config.scoped["DocumentLibrary"]["aspects"];

   return (
   {
      visible: getConfigAspects(scriptRoot, scopedRoot, "visible"),
      addable: getConfigAspects(scriptRoot, scopedRoot, "addable"),
      removeable: getConfigAspects(scriptRoot, scopedRoot, "removeable")
   });
}

function getConfigAspects(scriptRoot, scopedRoot, childName)
{
   var aspects = [],
      aspectName,
      configs;

   // Try scoped config (override defaults)
   try
   {
      configs = scopedRoot.getChild(childName).childrenMap["aspect"];
      if (configs)
      {
         for (var i = 0, aspectName; i < configs.size(); i++)
         {
            // Get aspect qname from each config item
            aspectName = configs.get(i).attributes["name"];
            if (aspectName)
            {
               aspects.push(aspectName.toString());
            }
         }

         return aspects;
      }
   }
   catch (e)
   {
   }

   // Fallback to local script config
   for each (aspectName in scriptRoot[childName].aspect.@name)
   {
      aspects.push(aspectName.toString());
   }
   return aspects;
}

model.aspects = main();