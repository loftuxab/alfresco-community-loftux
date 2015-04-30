<#include "../admin-template.ftl" />

<@page title=msg("ldap.authentication.page.title", urldecode(args.id)) dialog=true>
   
   <div class="column-full">
      <p class="intro">${msg("ldap.authentication.page.intro-text")?html}</p>   
      <p class="info">${msg("ldap.authentication.page.instruction-link")}</p>   
   </div>
   
   <div class="column-full">
      <@section label=msg("ldap.authentication.section") />
      <@attrcheckbox attribute=attributes["ldap.authentication.active"] label=msg("ldap.authentication.active") description=msg("ldap.authentication.active.description") />
      <@attrtext attribute=attributes["ldap.authentication.userNameFormat"] label=msg("ldap.authentication.userNameFormat") description=msg("ldap.authentication.userNameFormat.description") escape=false />
   </div>
   <div class="column-left">
      <@attrtext attribute=attributes["ldap.authentication.java.naming.provider.url"] label=msg("ldap.authentication.java.naming.provider.url") description=msg("ldap.authentication.java.naming.provider.url.description") />
      <@attrtext attribute=attributes["ldap.authentication.defaultAdministratorUserNames"] label=msg("ldap.authentication.defaultAdministratorUserNames") description=msg("ldap.authentication.defaultAdministratorUserNames.description") />
   </div>
   <div class="column-right">
      <@attroptions attribute=attributes["ldap.authentication.java.naming.security.authentication"] label=msg("ldap.authentication.java.naming.security.authentication") description=msg("ldap.authentication.java.naming.security.authentication.description")>
         <@option label="simple" value="simple" />
         <@option label="DIGEST-MD5" value="DIGEST-MD5" />
         <@option label="CRAM-MD5" value="CRAM-MD5" />
         <@option label="GSSAPI" value="GSSAPI" />
         <@option label="GSS-SPNEGO" value="GSS-SPNEGO" />
         <@option label="NTLM" value="NTLM" />
      </@attroptions>
      <@attrcheckbox attribute=attributes["ldap.authentication.authenticateFTP"] label=msg("ldap.authentication.ldap.authentication.authenticateFTP") description=msg("ldap.authentication.ldap.authentication.authenticateFTP.description") />
   </div>
   
   <div class="column-full">
      <@section label=msg("ldap.synchronization.section") />
      <@attrcheckbox attribute=attributes["ldap.synchronization.active"] label=msg("ldap.synchronization.active") description=msg("ldap.synchronization.active.description") />
   </div>
   <div class="column-left">   
      <@attrtext attribute=attributes["ldap.synchronization.java.naming.security.principal"] label=msg("ldap.synchronization.java.naming.security.principal") description=msg("ldap.synchronization.java.naming.security.principal.description") />
      <@attrtext attribute=attributes["ldap.synchronization.groupQuery"] label=msg("ldap.synchronization.groupQuery") description=msg("ldap.synchronization.groupQuery.description") />
      <@attrtext attribute=attributes["ldap.synchronization.userSearchBase"] label=msg("ldap.synchronization.userSearchBase") description=msg("ldap.synchronization.userSearchBase.description") />
      <@attrtext attribute=attributes["ldap.synchronization.personQuery"] label=msg("ldap.synchronization.personQuery") description=msg("ldap.synchronization.personQuery.description") />
   </div>
   <div class="column-right">   
      <@attroptions attribute=attributes["ldap.synchronization.java.naming.security.authentication"] label=msg("ldap.synchronization.java.naming.security.authentication") description=msg("ldap.synchronization.java.naming.security.authentication.description")>
         <@option label="simple" value="simple" />
         <@option label="DIGEST-MD5" value="DIGEST-MD5" />
         <@option label="CRAM-MD5" value="CRAM-MD5" />
         <@option label="GSSAPI" value="GSSAPI" />
         <@option label="GSS-SPNEGO" value="GSS-SPNEGO" />
         <@option label="NTLM" value="NTLM" />
      </@attroptions>
      <@attrpassword attribute=attributes["ldap.synchronization.java.naming.security.credentials"] label=msg("ldap.synchronization.java.naming.security.credentials") description=msg("ldap.synchronization.java.naming.security.credentials.description") visibilitytoggle=true populatevalue=true />
      <@attrtext attribute=attributes["ldap.synchronization.groupSearchBase"] label=msg("ldap.synchronization.groupSearchBase") description=msg("ldap.synchronization.groupSearchBase.description") />
      <@attrtext attribute=attributes["ldap.synchronization.personDifferentialQuery"] label=msg("ldap.synchronization.personDifferentialQuery") description=msg("ldap.synchronization.personDifferentialQuery.description") />
   </div>
   
   <div class="column-full">
      <@tsection label=msg("ldap.synchronization-advanced.section")>
         <p class="info">${msg("ldap.synchronization-advanced.section.description")?html}</p>
         <div class="column-left">
            <@attrtext attribute=attributes["ldap.synchronization.defaultHomeFolderProvider"] label=msg("ldap.synchronization.defaultHomeFolderProvider") description=msg("ldap.synchronization.defaultHomeFolderProvider.description") />
            <@attrtext attribute=attributes["ldap.synchronization.groupDifferentialQuery"] label=msg("ldap.synchronization.groupDifferentialQuery") description=msg("ldap.synchronization.groupDifferentialQuery.description") />
            <@attrtext attribute=attributes["ldap.synchronization.groupIdAttributeName"] label=msg("ldap.synchronization.groupIdAttributeName") description=msg("ldap.synchronization.groupIdAttributeName.description") />
            <@attrtext attribute=attributes["ldap.synchronization.groupType"] label=msg("ldap.synchronization.groupType") description=msg("ldap.synchronization.groupType.description") />
            <@attrtext attribute=attributes["ldap.synchronization.personType"] label=msg("ldap.synchronization.personType") description=msg("ldap.synchronization.personType.description") />
            <@attrtext attribute=attributes["ldap.synchronization.userEmailAttributeName"] label=msg("ldap.synchronization.userEmailAttributeName") description=msg("ldap.synchronization.userEmailAttributeName.description") />
            <@attrtext attribute=attributes["ldap.synchronization.userIdAttributeName"] label=msg("ldap.synchronization.userIdAttributeName") description=msg("ldap.synchronization.userIdAttributeName.description") />
            <@attrtext attribute=attributes["ldap.synchronization.userOrganizationalIdAttributeName"] label=msg("ldap.synchronization.userOrganizationalIdAttributeName") description=msg("ldap.synchronization.userOrganizationalIdAttributeName.description") />
            <@attrtext attribute=attributes["ldap.synchronization.queryBatchSize"] label=msg("ldap.synchronization.queryBatchSize") description=msg("ldap.synchronization.queryBatchSize.description") />
            <@attrtext attribute=attributes["ldap.synchronization.com.sun.jndi.ldap.connect.pool"] label=msg("ldap.synchronization.com.sun.jndi.ldap.connect.pool") description=msg("ldap.synchronization.com.sun.jndi.ldap.connect.pool.description") />
            <@attrtext attribute=attributes["ldap.pooling.com.sun.jndi.ldap.connect.pool.authentication"] label=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.authentication") description=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.authentication.description") />
            <@attrtext attribute=attributes["ldap.pooling.com.sun.jndi.ldap.connect.pool.debug"] label=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.debug") description=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.debug.description") />
            <@attrtext attribute=attributes["ldap.pooling.com.sun.jndi.ldap.connect.pool.protocol"] label=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.protocol") description=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.protocol.description") />
         </div>
         <div class="column-right">
            <@attrtext attribute=attributes["ldap.synchronization.groupDisplayNameAttributeName"] label=msg("ldap.synchronization.groupDisplayNameAttributeName") description=msg("ldap.synchronization.groupDisplayNameAttributeName.description") />
            <@attrtext attribute=attributes["ldap.synchronization.groupMemberAttributeName"] label=msg("ldap.synchronization.groupMemberAttributeName") description=msg("ldap.synchronization.groupMemberAttributeName.description") />
            <@attrtext attribute=attributes["ldap.synchronization.modifyTimestampAttributeName"] label=msg("ldap.synchronization.modifyTimestampAttributeName") description=msg("ldap.synchronization.modifyTimestampAttributeName.description") />
            <@attrtext attribute=attributes["ldap.synchronization.timestampFormat"] label=msg("ldap.synchronization.timestampFormat") description=msg("ldap.synchronization.timestampFormat.description") />
            <@attrtext attribute=attributes["ldap.synchronization.userFirstNameAttributeName"] label=msg("ldap.synchronization.userFirstNameAttributeName") description=msg("ldap.synchronization.userFirstNameAttributeName.description") />
            <@attrtext attribute=attributes["ldap.synchronization.userLastNameAttributeName"] label=msg("ldap.synchronization.userLastNameAttributeName") description=msg("ldap.synchronization.userLastNameAttributeName.description") />
            <@attrtext attribute=attributes["ldap.pooling.com.sun.jndi.ldap.connect.pool.initsize"] label=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.initsize") description=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.initsize.description") />
            <@attrtext attribute=attributes["ldap.pooling.com.sun.jndi.ldap.connect.pool.maxsize"] label=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.maxsize") description=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.maxsize.description") />
            <@attrtext attribute=attributes["ldap.pooling.com.sun.jndi.ldap.connect.pool.prefsize"] label=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.prefsize") description=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.prefsize.description") />
            <@attrtext attribute=attributes["ldap.pooling.com.sun.jndi.ldap.connect.pool.timeout"] label=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.timeout") description=msg("ldap.pooling.com.sun.jndi.ldap.connect.pool.timeout.description") />
            <@attrtext attribute=attributes["ldap.pooling.com.sun.jndi.ldap.connect.timeout"] label=msg("ldap.pooling.com.sun.jndi.ldap.connect.timeout") description=msg("ldap.pooling.com.sun.jndi.ldap.connect.timeout.description") />
         </div>
      </@tsection>
   </div>
   
   <@dialogbuttons save=true />
   
</@page>