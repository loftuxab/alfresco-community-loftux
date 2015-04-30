<#escape x as jsonUtils.encodeJSONString(x)>
{
   "domainsChecked":
   [
<#list domainValidityChecks?keys as domain>
   {
   <#if domainValidityChecks[domain]??>
      <#assign validityCheck=domainValidityChecks[domain]>
      "domain": "${domain}",
      <#if validityCheck.failureReason??>
      "isValid": false,
      "failureReason": "${validityCheck.failureReason}",
      "failureNotes": "${validityCheck.failureNotes!""}"
      <#else>
      "isValid": true,
      "failureReason": "",
      "failureNotes": ""
      </#if>
   </#if>
   }
   <#if (domain_has_next)>,</#if>
</#list>
   ]
}
</#escape>