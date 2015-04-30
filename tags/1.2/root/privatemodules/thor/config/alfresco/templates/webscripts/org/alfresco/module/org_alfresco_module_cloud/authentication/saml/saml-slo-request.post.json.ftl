<#escape x as jsonUtils.encodeJSONString(x)>
   {
      "SAMLResponse" : "${SAMLResponse}",
      "RelayState" : <#if RelayState??>"${RelayState}"<#else>null</#if>,
      "action" : "${action}",
      "userId" : "${userId}",
      "result" : "${result}"
   }
</#escape>