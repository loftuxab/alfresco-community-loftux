<#-- generic-paged-results.lib.ftl is in the RemoteAPI project -->

<#import "../../../repository/generic-paged-results.lib.ftl" as gen/>
<#import "./invalid-domains.lib.ftl" as invalidDomainsLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
   <@gen.pagedResults data=data ; item>
      <@invalidDomainsLib.invalidDomainJSON item=item />
   </@gen.pagedResults>
   }
}
</#escape>
