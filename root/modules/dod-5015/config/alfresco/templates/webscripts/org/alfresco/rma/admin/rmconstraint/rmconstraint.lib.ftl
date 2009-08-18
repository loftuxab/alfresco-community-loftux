<#-- renders an rm constraint object -->

<#macro constraintJSON constraintDetail>
<#escape x as jsonUtils.encodeJSONString(x)>
{
     "authorityName" : "${constraintDetail.authorityName}",
     "values" : [<#list constraintDetail.values as value> "${value}"<#if value_has_next>,</#if></#list> ] 
}
</#escape>
</#macro>

