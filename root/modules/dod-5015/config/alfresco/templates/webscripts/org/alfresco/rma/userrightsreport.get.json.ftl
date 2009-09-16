<#escape x as jsonUtils.encodeJSONString(x)>
{
	"data":
	{
	    "users":
		{
			<#list report.users?keys as user>			
            "${user}":
            {
                "userName": "${report.users[user].userName!""}",
                "firstName": "${report.users[user].firstName!""}",
                "lastName": "${report.users[user].lastName!""}",
                "roles": [<#list report.users[user].roles as role>"${role}"<#if role_has_next>,</#if></#list>]
            }
            <#if user_has_next>,</#if>
            </#list>
		},
		"roles":
		{
			<#list report.roles?keys as role>         
            "${role}":
            {
                "name": "${report.roles[role].name!""}",
                "label": "${report.roles[role].displayLabel!""}",
                "users": [<#list report.roles[role].users as user>"${user}"<#if user_has_next>,</#if></#list>],
                "capabilities": [<#list report.roles[role].capabilities as capability>"${capability}"<#if capability_has_next>,</#if></#list>]
            }
            <#if role_has_next>,</#if>
            </#list>
		}
	}
}
</#escape>