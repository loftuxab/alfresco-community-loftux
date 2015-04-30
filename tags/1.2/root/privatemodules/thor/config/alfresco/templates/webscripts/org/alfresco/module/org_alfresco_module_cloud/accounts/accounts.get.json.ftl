<#-- generic-paged-results.lib.ftl is in the RemoteAPI project -->

<#import "../../../repository/generic-paged-results.lib.ftl" as gen/>
<#import "./accounts.lib.ftl" as accountsLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
   <@gen.pagedResults data=data ; item>
      {
      <@accountsLib.accountJSON item=item />
      }
   </@gen.pagedResults>
   }
}
</#escape>
