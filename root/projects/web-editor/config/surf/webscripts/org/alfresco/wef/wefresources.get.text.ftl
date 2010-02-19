<#escape x as jsonUtils.encodeJSONString(x)>
<#list resources as resource>
WEF.addResource(
{
   name: "${resource.name}", 
   type: "${resource.type}",
   path: "${resource.path}"
});
</#list>

WEF.run("${appName}");
</#escape>