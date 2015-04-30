<#escape x as jsonUtils.encodeJSONString(x)>
   {
      "userId": <#if userId??>"${userId}"<#else>null</#if>
   }
</#escape>
