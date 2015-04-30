<#escape x as jsonUtils.encodeJSONString(x)>
{
   "status": "${status?html}"<#if status=="table">,
   "nodeNames": [
                <#list nodeNames as name>
                  "${name?html}"<#if name_has_next>,</#if>
                </#list>
                ],
   "tableData": [
                <#list validation as rowResults>
                  [
                  <#list rowResults as result>
                     "${result}"<#if result_has_next>,</#if>
                  </#list>
                  ]<#if rowResults_has_next>,</#if>
                </#list>
                ]<#elseif status=="message">,
    "message": "${message?html}"</#if>
}
</#escape>