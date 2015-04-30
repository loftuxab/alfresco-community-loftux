<#import "./accounts.lib.ftl" as accountsLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
      "success" : ${success?string}
   }
}
</#escape>
