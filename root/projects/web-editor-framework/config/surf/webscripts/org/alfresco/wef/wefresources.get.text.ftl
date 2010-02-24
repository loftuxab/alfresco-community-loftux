<#macro renderResource resource>
<#assign path = resource.path?interpret>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   name: "${resource.name}", 
   type: "${resource.type}",
   path: "${url.context}<@path />"<#if resource.dependencies?size &gt; 0>,
   requires: [<#list resource.dependencies as dependency>"${dependency.name}"<#if dependency_has_next>,</#if></#list>]</#if>
}
</#escape>
</#macro>

<#list resources as resource>
<#if resource.path??>
WEF.addResource(<@renderResource resource=resource />);
</#if>
</#list>

WEF.run("${appName?html}");