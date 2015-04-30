<#escape x as jsonUtils.encodeJSONString(x)>
   {
      "emailAddress": "${emailAddress}", 
      "domain": "${validityCheck.domain!""}",
<#if validityCheck.failureReason??>
      "isValid": false,
      "failureReason": "${validityCheck.failureReason}",
      "failureNotes": "${validityCheck.failureNotes!""}"
<#else>
      "isValid": true,
      "failureReason": "",
      "failureNotes": ""
</#if>
   }
</#escape>
