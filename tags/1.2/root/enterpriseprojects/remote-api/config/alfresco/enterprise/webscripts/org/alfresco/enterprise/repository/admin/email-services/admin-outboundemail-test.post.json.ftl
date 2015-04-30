<#escape x as jsonUtils.encodeJSONString(x)>
{
   "success": ${success?string}<#if error??>,
   "error": "${error?string}"
   </#if>
}
</#escape>