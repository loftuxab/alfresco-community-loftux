<#escape x as jsonUtils.encodeJSONString(x)>
{
   "userName": "${userName!""}",
   "authenticatorName": "${authenticatorName!""}",
<#if authenticationMessage??>
   "authenticationMessage": "${authenticationMessage}",
</#if>
<#if diagnostic??>
   "diagnostic":
   [
   <#list diagnostic.steps as s>
      {
         "success": ${s.success?string},
         "message": "${s.message}"
      }<#if s_has_next>,</#if>
   </#list>
   ],
</#if>
<#if users??>
   "users":
   [
   <#list users as u>
      {"id": "${u}"}<#if u_has_next>,</#if>
   </#list>
   ],
</#if>
<#if groups??>
   "groups":
   [
   <#list groups as g>
      {"id": "${g}"}<#if g_has_next>,</#if>
   </#list>
   ],
</#if>
<#if syncActive??>
  "syncActive": ${syncActive?string},
</#if>
   "testPassed": ${testPassed?string}
}
</#escape>