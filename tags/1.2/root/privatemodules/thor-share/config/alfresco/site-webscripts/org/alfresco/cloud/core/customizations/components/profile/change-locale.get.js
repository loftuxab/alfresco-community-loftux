<import resource="classpath:alfresco/site-webscripts/org/alfresco/cloud/core/customizations/locales.lib.js">
function main()
{
   // Add the available languages to an array
   model.languages = getLocales();
   
   /* 
    * Set the current language to be selected in the dropdown
    */
   for(var x = 0; x < model.languages.length; x++)
   {
      if(model.languages[x].locale === locale)
      {
         model.languages[x].selected = true;
         break;
      }
   }
}

main();