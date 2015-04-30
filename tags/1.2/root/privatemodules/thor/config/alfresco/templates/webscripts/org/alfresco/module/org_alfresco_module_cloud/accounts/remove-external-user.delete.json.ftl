<#import "./accounts.lib.ftl" as accountsLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
      <#-- do not render this userExists flag in the response -->
      <#if userExists?? && defaultAccount?? && homeAccount??>
      <@accountsLib.userAccountsJSON defaultAccount homeAccount secondaryAccounts/>
      </#if>
   }
}
</#escape>
