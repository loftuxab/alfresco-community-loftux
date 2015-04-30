<import resource="classpath:alfresco/enterprise/webscripts/org/alfresco/enterprise/repository/admin/admin-common.lib.js">

/**
 * Repository Admin Console
 *
 * Authentication Subsystem LDAP Get
 */
function main()
{
   var id = args.id;
   if (id == null)
   {
      status.code = 400;
      status.message = "Sub-system name not been provided.";
      status.redirect = true;
      return;
   }
   
   var bean = "Alfresco:Type=Configuration,Category=Authentication,id1=managed,id2=" + id;
   if (jmx.queryMBeans(bean).length === 0)
   {
      status.code = 400;
      status.message = "Unknown authentication ID provided: " + id;
      status.redirect = true;
      return;
   }
   
   model.attributes = Admin.getMBeanAttributes(
      bean, [
         "ldap.authentication.active", 
         "ldap.authentication.userNameFormat",
         "ldap.authentication.java.naming.provider.url",
         "ldap.authentication.defaultAdministratorUserNames",
         "ldap.authentication.java.naming.security.authentication",
         "ldap.authentication.authenticateFTP",
         
         "ldap.synchronization.active",
         "ldap.synchronization.java.naming.security.principal",
         "ldap.synchronization.groupQuery",
         "ldap.synchronization.userSearchBase",
         "ldap.synchronization.personQuery",
         "ldap.synchronization.java.naming.security.authentication",
         "ldap.synchronization.java.naming.security.credentials",
         "ldap.synchronization.groupSearchBase",
         "ldap.synchronization.personDifferentialQuery",
         
         "ldap.synchronization.defaultHomeFolderProvider",
         "ldap.synchronization.groupDifferentialQuery",
         "ldap.synchronization.groupIdAttributeName",
         "ldap.synchronization.groupType",
         "ldap.synchronization.personType",
         "ldap.synchronization.userEmailAttributeName",
         "ldap.synchronization.userIdAttributeName",
         "ldap.synchronization.userOrganizationalIdAttributeName", 
         "ldap.synchronization.queryBatchSize",     
         "ldap.synchronization.groupDisplayNameAttributeName",
         "ldap.synchronization.groupMemberAttributeName",
         "ldap.synchronization.modifyTimestampAttributeName",
         "ldap.synchronization.timestampFormat",
         "ldap.synchronization.userFirstNameAttributeName",
         "ldap.synchronization.userLastNameAttributeName",

         "ldap.synchronization.com.sun.jndi.ldap.connect.pool",
         "ldap.pooling.com.sun.jndi.ldap.connect.pool.authentication",
         "ldap.pooling.com.sun.jndi.ldap.connect.pool.debug",
         "ldap.pooling.com.sun.jndi.ldap.connect.pool.initsize",
         "ldap.pooling.com.sun.jndi.ldap.connect.pool.maxsize",
         "ldap.pooling.com.sun.jndi.ldap.connect.pool.prefsize",
         "ldap.pooling.com.sun.jndi.ldap.connect.pool.protocol",
         "ldap.pooling.com.sun.jndi.ldap.connect.pool.timeout",
         "ldap.pooling.com.sun.jndi.ldap.connect.timeout"
      ]
   );
}

main();