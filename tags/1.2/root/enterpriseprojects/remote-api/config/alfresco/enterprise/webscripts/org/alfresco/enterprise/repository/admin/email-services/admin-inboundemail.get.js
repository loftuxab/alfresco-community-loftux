<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * Inbound Email GET method
 */
function main()
{
   model.attributes = Admin.getMBeanAttributes(
      "Alfresco:Type=Configuration,Category=email,id1=inbound",
      ["email.server.enabled", "email.inbound.enabled", "email.inbound.unknownUser", "email.server.allowed.senders", "email.handler.folder.overwriteDuplicates", "email.server.connections.max", "email.server.auth.enabled", "email.server.port", "email.server.domain", "email.server.blocked.senders", "email.inbound.emailContributorsAuthority", "email.server.hideTLS", "email.server.enableTLS", "email.server.requireTLS"]
   );
   
   model.tools = Admin.getConsoleTools("admin-inboundemail"); 
   model.metadata = Admin.getServerMetaData();
   
   /*
    * Set the selected option for the TLS dropdown based on the 
    * values of the email.server.hideTLS, email.server.enableTLS and email.server.requireTLS
    */
   model.selectedTLS = "";
   if (model.attributes["email.server.hideTLS"].value == "true")
   {
      model.selectedTLS = "email.server.hideTLS";
   }
   else if (model.attributes["email.server.enableTLS"].value == "true")
   {
      model.selectedTLS = "email.server.enableTLS";
   }
   else if (model.attributes["email.server.requireTLS"].value == "true")
   {
      model.selectedTLS = "email.server.requireTLS";
   }
   
   /*
    * Set the value of the email enabled checkbox based on the values of email.server.enabled and email.inbound.enabled
    * Both must be true for the checkbox to be checked.
    * 
    * Create a dummy attribute that can be used to populate an attribute checkbox field - the value in this field is then
    * used to populate the two underlying attributes (both of which are hidden attribute fields).
    */
   var inboundEnabled = (model.attributes["email.server.enabled"].value == "true" && model.attributes["email.inbound.enabled"].value == "true") ? "true" : "false";
   model.attributes["inboundEnabled"] = {
      qname: "",  // set empty to ensure the checkbox we populate isn't POSTed with the form data
      name:  "inboundEnabled",
      value: inboundEnabled,
      description: "",
      type: "",
      readonly: false
   };
}

main();