<#escape x as jsonUtils.encodeJSONString(x)>
{
   "email" : "${email}",
   "id" : "${id}",
   "key" : "${key}",
   "isRegistered" : ${isRegistered?string},
   "isActivated" : ${isActivated?string},
   <#if initiatorFirstName??>"initiatorFirstName": "${initiatorFirstName}",</#if>
   <#if initiatorLastName??>"initiatorLastName": "${initiatorLastName}",</#if>
   "isPreRegistered": ${isPreRegistered?string}
}
</#escape>