<#escape x as jsonUtils.encodeJSONString(x)>
{
   "loginValid": ${loginValid?string},
   "username": "${username}",
   "remoteSystemAvailable": ${remoteSystemAvailable?string}
   <#if message??>
      , "message": "${message}"
   </#if>
}
</#escape>
