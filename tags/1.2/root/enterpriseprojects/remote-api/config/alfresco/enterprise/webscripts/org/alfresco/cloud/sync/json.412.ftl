<#-- Sync Node related problem -->
{
  <#escape x as jsonUtils.encodeJSONString(x)>
    "message": "${message}",
    "messageId": "${messageId}"
    <#if cause??>
       , "cause": {
          "message": "${cause.message}"
          <#if cause.class??>
             , "class": "${cause.class}"
          </#if>
          <#if cause.stacktrace??>
             , "stacktrace": [
             <#list cause.stacktrace as st>
                "${st}"<#if st_has_next>,</#if>
             </#list>
             ]
          </#if>
       }
    </#if>
  </#escape>
}
