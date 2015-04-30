function main()
{
   // Widget instantiation metadata...
   var samlSettings = {
      id : "SAMLSettings",
      name : "Alfresco.cloud.component.SAMLSettings",
      options :
      {
         detailsWebscript: "saml/config"
      }
   };

   model.widgets = [samlSettings];
}

main();