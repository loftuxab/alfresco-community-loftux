<#escape x as jsonUtils.encodeJSONString(x)>
<#list resources as resource>
<#if resource.path??>
WEF.addResource({
   name: "${resource.name}", 
   type: "${resource.type}",
   path: "${url.context}${resource.path}"<#if resource.dependencies?size &gt; 0>,
   requires: [<#list resource.dependencies as dependency>"${dependency.name}"<#if dependency_has_next>,</#if></#list>]</#if>
});
</#if>
</#list>

WEF.run("${appName}");
</#escape>