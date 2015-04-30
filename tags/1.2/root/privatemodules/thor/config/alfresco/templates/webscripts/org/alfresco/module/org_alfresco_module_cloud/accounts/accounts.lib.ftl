<#--
   This template renders an email registration
-->
<#macro registrationJSON item>
<#escape x as jsonUtils.encodeJSONString(x)>
   {
      "email": "${item.emailAddress}",
      "registrationDate": <#if item.registrationDate??>"${xmldate(item.registrationDate)}"<#else>null</#if>,
      "id": <#if item.id??>"${item.id}"<#else>null</#if>,
      "key": <#if item.key??>"${item.key}"<#else>null</#if>
   }
</#escape>
</#macro>

<#--
   This template renders an account info object
-->
<#macro accountJSON item>
<#escape x as jsonUtils.encodeJSONString(x)>
   "id": ${item.id?c},
   "name": "${item.name}",
   "type": ${item.type.id?c},
   "typeDisplayName": "${item.type.displayName}",
   "enabled" : ${item.enabled?string},
   "className": "${item.accountClassName}",
   "classDisplayName": "${item.accountClassDisplayName}",
   "creationDate": "${xmldate(item.creationDate)}",
   "usageQuota":
   {
      "fileUploadSizeUQ": { "q" : ${item.usageQuota.fileUploadQuota?c} },
      "fileSizeUQ": { "u" : ${item.usageQuota.fileUsage?c}, "q" : ${item.usageQuota.fileQuota?c} },
      "siteCountUQ": { "u" : ${item.usageQuota.siteCountUsage?c}, "q" : ${item.usageQuota.siteCountQuota?c} },
      "personCountUQ": { "u" : ${item.usageQuota.personCountUsage?c}, "q": ${item.usageQuota.personCountQuota?c} },
      "personIntOnlyCountUQ": { "u" : ${item.usageQuota.personIntOnlyCountUsage?c}, "q" : ${item.usageQuota.personIntOnlyCountQuota?c} },
      "personNetworkAdminCountUQ": { "u" : ${item.usageQuota.personNetworkAdminCountUsage?c}, "q" : ${item.usageQuota.personNetworkAdminCountQuota?c} }
   },
   "domains":
   [
      <#list item.domains as domain>
      "${domain}"<#if domain_has_next>,</#if>
      </#list>
   ],
   "tenant": "${item.tenantId}"
</#escape>
</#macro>

<#--
   This template renders all the account info for a user
-->
<#macro userAccountsJSON defaultAccount homeAccount secondaryAccounts>
<#escape x as jsonUtils.encodeJSONString(x)>
  <#if defaultAccount??>
  "default": ${defaultAccount?c},
  </#if>
  <#if homeAccount??>
  "home": { <@accountsLib.accountJSON item=homeAccount/> },
  </#if>
  "secondary": [
  <#list secondaryAccounts as account>
    { <@accountsLib.accountJSON item=account/> }<#if account_has_next>,</#if>
  </#list>
  ]
</#escape>
</#macro>