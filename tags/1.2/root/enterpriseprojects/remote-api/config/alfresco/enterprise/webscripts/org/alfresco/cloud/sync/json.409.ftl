<#-- Conflict -->
{
   <#if message??>
      <#escape x as jsonUtils.encodeJSONString(x)>
         "message": "${message}"
      </#escape>
   </#if>
}
