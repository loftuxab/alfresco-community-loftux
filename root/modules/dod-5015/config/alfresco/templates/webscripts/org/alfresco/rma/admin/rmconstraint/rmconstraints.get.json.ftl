<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":

        [
            <#list constraintNames as constraintName>   
              {
              "url" : "${url.serviceContext + "/api/rma/admin/rmconstraints/" + constraintName}",
              "constraintName" : "${constraintName}"
              }
               <#if constraintName_has_next>,</#if>
            </#list>
        ]

}
</#escape>