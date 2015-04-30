<#import "./accounts.lib.ftl" as accountsLib/>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
   <@accountsLib.accountJSON item=account />
   }
}
</#escape>