{
"invalidEmails" : [
    <#list invalidEmails as invalidEmail>
        <@invalidEmailJSON invalidEmail=invalidEmail/>
        <#if invalidEmail_has_next>,</#if>
    </#list>
]
}

<#macro invalidEmailJSON invalidEmail>
<#escape x as jsonUtils.encodeJSONString(x)>
{
    "email": "${invalidEmail.email}",
    "failureReason": "${invalidEmail.type}"
}
</#escape>
</#macro>