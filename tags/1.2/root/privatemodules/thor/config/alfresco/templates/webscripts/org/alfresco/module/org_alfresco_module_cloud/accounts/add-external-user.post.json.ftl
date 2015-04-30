<#import "./accounts.lib.ftl" as accountsLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
      <@accountsLib.userAccountsJSON defaultAccount homeAccount secondaryAccounts/>
   }
}
</#escape>
