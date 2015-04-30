<#escape x as jsonUtils.encodeJSONString(x)>
{
   "known": ${known?string}
   <#if known>
      ,
      "username": "${username}",
      "lastLoginSuccessful": ${lastLoginSuccessful?string}
   </#if>
}
</#escape>
