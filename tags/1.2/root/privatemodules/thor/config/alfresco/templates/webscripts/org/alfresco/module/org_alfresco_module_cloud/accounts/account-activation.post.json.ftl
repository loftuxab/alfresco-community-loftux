<#import "./accounts.lib.ftl" as accountsLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
      "registration" : <@accountsLib.registrationJSON item=registration/>, 
      <@accountsLib.userAccountsJSON defaultAccount homeAccount secondaryAccounts/>
   }
}
</#escape>
