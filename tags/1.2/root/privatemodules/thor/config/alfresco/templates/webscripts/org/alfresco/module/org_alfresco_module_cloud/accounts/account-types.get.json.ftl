<#-- generic-paged-results.lib.ftl is in the RemoteAPI project -->

<#import "../../../repository/generic-paged-results.lib.ftl" as gen/>
<#import "./account-type.lib.ftl" as accountTypeLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
   <@gen.pagedResults data=data ; item>
      {
         <@accountTypeLib.accountTypeJSON item=item />
      }
   </@gen.pagedResults>
   }
}
</#escape>
