<#-- Renders list of ssdIds (SyncSetDefinition GUIDs) -->
<#macro resultsJSON ssdIds>
<#escape x as jsonUtils.encodeJSONString(x)>
[
<#if ssdIds??>
<#list ssdIds as ssdId>
   "${ssdId}"<#if ssdId_has_next>,</#if>
</#list>
</#if>
]
</#escape>
</#macro>
