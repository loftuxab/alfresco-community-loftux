<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 * 
 * User and Group Directory Management GET method
 */
function main()
{
   model.attributes = Admin.getMBeanAttributes(
      "Alfresco:Type=Configuration,Category=Authentication,id1=manager",
      ["chain"]
   );
   
   // split auth chain into component parts
   // e.g. alfrescoNtlm1:alfrescoNtlm,ldap1:ldap-ad
   var AUTH_BEAN_PREFIX = "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=";
   var auths = [],
       chain = String(model.attributes["chain"].value).split(",");
   for (var i=0; i<chain.length; i++)
   {
      var id = chain[i].split(":")[0],
          name = decodeURIComponent(id),
          type = chain[i].split(":")[1],
          removable = true,
          testable = true,
          synchable = false,
          synched = false,
          active = true,
          cifs = true,
          cifsSelected = false,
          browser = true,
          browserSelected = false,
          syncStatus = [];
      
      // Set the CIFS and Browser authentication settings - each authentication sub-system has its own set of
      // properties - with no common naming pattern, so each type must be managed individually.
      // In addition, only some types support CIFS/Browser authentication and only some support synchronization.
      switch (type)
      {
         case "alfrescoNtlm":
            removable = false;
            var alfrescoAttributes = Admin.getMBeanAttributes(
                                        AUTH_BEAN_PREFIX + id,
                                        ["alfresco.authentication.authenticateCIFS", "ntlm.authentication.sso.enabled"]
                                     );
            var cifsAttr = alfrescoAttributes["alfresco.authentication.authenticateCIFS"];
            if (cifsAttr) cifsSelected = (cifsAttr.value == "true");
            var browserAttr = alfrescoAttributes["ntlm.authentication.sso.enabled"];
            if (browserAttr) browserSelected = (browserAttr.value == "true");
            break;
         
         case "ldap":
         case "ldap-ad":
            synchable = true;
            var ldapAttributes = Admin.getMBeanAttributes(
                                    AUTH_BEAN_PREFIX + id,
                                    ["ldap.synchronization.active", "ldap.authentication.active"]
                                 );
            var syncAttr = ldapAttributes["ldap.synchronization.active"];
            if (syncAttr) synched = (syncAttr.value == "true");
            var activeAttr = ldapAttributes["ldap.authentication.active"];
            if (activeAttr) active = (activeAttr.value == "true");
            cifs = browser = false;
            
            // search for sync status information that may exist for this authenticator
            // TODO: need to retrieve this data as JSON for Ajax update of Sync Status panel
            var statusBeans = jmx.queryMBeans("Alfresco:Name=BatchJobs,Type=Synchronization,Category=directory,id1=" + id + ",*");
            for (var b=0; b<statusBeans.length; b++)
            {
               var statusAttrs = Admin.getMBeanAttributes(
                     statusBeans[b].name,
                     ["StartTime", "EndTime", "PercentComplete", "TotalResults", "TotalErrors", "SuccessfullyProcessedEntries"]
                  );
               var beanName = statusBeans[b].name;
               syncStatus.push({
                  // strip the id of the batch job bean
                  id: beanName.substring(beanName.indexOf(",id2=") + 5),
                  startTime: statusAttrs["StartTime"].value,
                  endTime: statusAttrs["EndTime"].value,
                  percentComplete: statusAttrs["PercentComplete"].value,
                  totalResults: statusAttrs["TotalResults"].value,
                  totalErrors: statusAttrs["TotalErrors"].value,
                  successfullyProcessedEntries: statusAttrs["SuccessfullyProcessedEntries"].value
               });
            }
            break;
         
         case "passthru":
            var passthruAttributes = Admin.getMBeanAttributes(
                                        AUTH_BEAN_PREFIX + id,
                                        ["passthru.authentication.authenticateCIFS", "ntlm.authentication.sso.enabled"]
                                     );
            var cifsAttr = passthruAttributes["passthru.authentication.authenticateCIFS"];
            if (cifsAttr) cifsSelected = (cifsAttr.value == "true");
            var browserAttr = passthruAttributes["ntlm.authentication.sso.enabled"];
            if (browserAttr) browserSelected = (browserAttr.value == "true");
            testable = false;
            break;
         
         case "kerberos":
            var kerberosAttributes = Admin.getMBeanAttributes(
                                        AUTH_BEAN_PREFIX + id,
                                        ["kerberos.authentication.authenticateCIFS", "kerberos.authentication.sso.enabled"]
                                     );
            var cifsAttr = kerberosAttributes["kerberos.authentication.authenticateCIFS"];
            if (cifsAttr) cifsSelected = (cifsAttr.value == "true");
            var browserAttr = kerberosAttributes["kerberos.authentication.sso.enabled"];
            if (browserAttr) browserSelected = (browserAttr.value == "true");
            testable = false;
            break;
         
         case "external":
            var extAttributes = Admin.getMBeanAttributes(
                                   AUTH_BEAN_PREFIX + id,
                                   ["external.authentication.enabled"]
                                );
            var activeAttr = extAttributes["external.authentication.enabled"];
            if (activeAttr) active = (activeAttr.value == "true");
            testable = cifs = browser = false;
            break;
      }
      
      // object structure representing the auth chain settings
      auths.push({
         id: id,
         name: name,
         type: type,
         removable: removable,
         testable: testable,
         synchable: synchable,
         synched: synched,
         active: active,
         cifs: cifs,
         cifsSelected: cifsSelected,
         browser: browser,
         browserSelected: browserSelected,
         syncStatus: syncStatus
      });
   }
   
   // common sync status of the Synchronization batch job manager bean
   var syncStatus = "";
   var statusManager = Admin.getMBean("Alfresco:Name=BatchJobs,Type=Synchronization,Category=manager");
   if (statusManager)
   {
      var statusAttrs = Admin.getMBeanAttributes(
                           "Alfresco:Name=BatchJobs,Type=Synchronization,Category=manager",
                           ["SynchronizationStatus"]
                        );
      syncStatus = statusAttrs["SynchronizationStatus"].value;
   }
   
   model.auths = auths;
   model.syncStatus = syncStatus;
   model.tools = Admin.getConsoleTools("admin-directorymanagement");
   model.metadata = Admin.getServerMetaData();
}

main();