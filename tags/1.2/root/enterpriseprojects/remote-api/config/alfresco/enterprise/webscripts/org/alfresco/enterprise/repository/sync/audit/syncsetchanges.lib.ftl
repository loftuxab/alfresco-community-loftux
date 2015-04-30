<#-- Renders list of nodeRefs -->
<#macro resultsJSON nodeRefs>
<#escape x as jsonUtils.encodeJSONString(x)>
[
<#if nodeRefs??>
<#list nodeRefs as nodeRef>
   "${nodeRef?string}"<#if nodeRef_has_next>,</#if>
</#list>
</#if>
]
</#escape>
</#macro>
