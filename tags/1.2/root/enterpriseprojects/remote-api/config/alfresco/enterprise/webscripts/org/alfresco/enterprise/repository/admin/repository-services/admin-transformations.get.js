<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Tranformers Settings GET method
 */
function main()
{
   model.jodConvAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=OOoJodconverter,id1=default",
         ["jodconverter.enabled","jodconverter.maxTasksPerProcess","jodconverter.taskExecutionTimeout","jodconverter.officeHome","jodconverter.portNumbers","jodconverter.taskQueueTimeout"]
      );
   
   model.swfToolsAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=ContentTransformer,Type=pdf2swf",
         ["Available","VersionString"]
      );
   
   model.imageMagicAttributes = Admin.getMBeanAttributes(
         "Alfresco:Name=ContentTransformer,Type=ImageMagick",
         ["Available","VersionString"]
      );
   
   model.thirdPartyAttributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=thirdparty,id1=default",
         ["img.exe","img.root","img.dyn","swf.exe","swf.encoder.params","img.coders","img.gslib","img.config"]
      );
   
   model.tools = Admin.getConsoleTools("admin-transformations");
   model.metadata = Admin.getServerMetaData();
}

main();