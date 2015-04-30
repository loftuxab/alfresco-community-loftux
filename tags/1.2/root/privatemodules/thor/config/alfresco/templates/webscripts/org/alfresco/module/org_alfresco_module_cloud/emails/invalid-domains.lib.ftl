<#--
   This template renders an invalid domain
-->
<#macro invalidDomainJSON item>
<#escape x as jsonUtils.encodeJSONString(x)>
   {
      "domain": "${item.domain}",
      "type": "${item.failureReason?string}",
      "notes": "${item.failureNotes!""}"
   }
</#escape>
</#macro>
