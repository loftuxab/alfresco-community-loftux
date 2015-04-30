<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * User and Group Directory Management POST method
 */
function main()
{
   var returnParams = "m=admin-console.success";
   try
   {
      // persist the auth chain changes
      Admin.persistJMXFormData();
      
      // extract the other properties to save manually
      var cifsId = "";
      var browserId = "";
      for each (field in formdata.fields)
      {
         switch (String(field.name))
         {
            case "AdminDM-cifs":
               cifsId = field.value;
               break;
            
            case "AdminDM-browser":
               browserId = field.value;
               break;
         }
      }
      
      // walk the auth chain sub-system beans and set CIFS/Browser SSO login property appropriately on each
      model.attributes = Admin.getMBeanAttributes(
         "Alfresco:Type=Configuration,Category=Authentication,id1=manager",
         ["chain"]
      );
      
      //
      // NOTE: May be unable to retrieve beans that have only just been created (persisted for the first time!) above?? - if so
      //       will ONLY be able to set CIFS/Browser to an *active* bean - i.e. only show those in the list - else will be disabled.
      //       What happens with the default=true that some of the beans have for cifs/sso??! - won't that override what happens below
      //       if a new subsystem is created but not set *because can't be retrieved yet?* do they start async??
      //
      
      // keep track of beans to persist later - must only retrieve each one once
      var persistBeans = {};
      var fnGetMBean = function(mbean) {
         if (!persistBeans[mbean])
         {
            var bean = Admin.getMBean(mbean);
            if (bean)
            {
               // cache this bean as dirty ready for save later
               persistBeans[mbean] = bean;
            }
         }
         return persistBeans[mbean];
      };
      
      // split auth chain into component parts
      // e.g. alfrescoNtlm1:alfrescoNtlm,ldap1:ldap-ad
      var AUTH_BEAN_PREFIX = "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=";
      var chain = String(model.attributes["chain"].value).split(",");
      for (var i=0; i<chain.length; i++)
      {
         var id = chain[i].split(":")[0],
             type = chain[i].split(":")[1];
         
         // test for valid sub-system types - only some support cifs/sso
         // if CIFS or Browser sub-system ID has been set then enable it and disable for all others
         switch (type)
         {
            case "alfrescoNtlm":
               var mbean = fnGetMBean(AUTH_BEAN_PREFIX + id);
               mbean.attributes["alfresco.authentication.authenticateCIFS"].value = (cifsId == id ? "true" : "false");
               mbean.attributes["ntlm.authentication.sso.enabled"].value = (browserId == id ? "true" : "false");
               break;
            
            case "passthru":
               var mbean = fnGetMBean(AUTH_BEAN_PREFIX + id);
               mbean.attributes["passthru.authentication.authenticateCIFS"].value = (cifsId == id ? "true" : "false");
               mbean.attributes["ntlm.authentication.sso.enabled"].value = (browserId == id ? "true" : "false");
               break;
            
            case "kerberos":
               var mbean = fnGetMBean(AUTH_BEAN_PREFIX + id);
               mbean.attributes["kerberos.authentication.authenticateCIFS"].value = (cifsId == id ? "true" : "false");
               mbean.attributes["kerberos.authentication.sso.enabled"].value = (browserId == id ? "true" : "false");
               break;
         }
      }
      
      // save the modified bean list
      for each (bean in persistBeans)
      {
         jmx.save(bean);
      }
   }
   catch (e)
   {
      returnParams = "e=" + e.message;
   }
   // generate the return URL
   status.code = 301;
   status.location = url.service + "?" + returnParams;
   status.redirect = true;
}

main();