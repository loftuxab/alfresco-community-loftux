function main()
{
   var scopedRoot = config.scoped["DocumentLibrary"]["aspects"];

   return (
   {
      visible: getConfigAspects(scopedRoot, "visible"),
      addable: getConfigAspects(scopedRoot, "addable"),
      removeable: getConfigAspects(scopedRoot, "removeable")
   });
}

function getConfigAspects(scopedRoot, childName)
{
   var aspects = [],
      aspectName,
      configs;

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
      }
   }
   catch (e)
   {
   }

   return aspects;
}

model.aspects = main();