<#--
   This template renders an account-type info object
-->
<#macro accountTypeJSON item>
<#escape x as jsonUtils.encodeJSONString(x)>
   "id": ${item.id?c},
   "name": "${item.displayName}",
   "quotas":
   {
      "fileSize": ${item.fileQuota?c},
      "siteCount": ${item.siteCountQuota?c},
      "personCount": ${item.personCountQuota?c},
      "personIntOnlyCount": ${item.personIntOnlyCountQuota?c},
      "personNetworkAdminCount": ${item.personNetworkAdminCountQuota?c}
   },
   "accountClass" :
   {
      "name": "${item.accountClass.name?string}",
      "displayName": "${item.accountClass.displayName}"
   }
</#escape>
</#macro>
