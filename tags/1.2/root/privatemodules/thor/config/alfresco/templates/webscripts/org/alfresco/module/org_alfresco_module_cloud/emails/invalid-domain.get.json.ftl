<#import "./invalid-domains.lib.ftl" as invalidDomainsLib/>
<#escape x as jsonUtils.encodeJSONString(x)>
   <@invalidDomainsLib.invalidDomainJSON item=domain />
</#escape>
