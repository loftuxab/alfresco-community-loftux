<#escape x as jsonUtils.encodeJSONString(x)>
{
	"data":
	{
		"roles":
		{
			<#list roles as role>
			"${role.name}":
			{
				"name": "${role.name}",
				"displayLabel": "${role.displayLabel}"
			}<#if prop_has_next>,</#if>
			</#list>
		}
	}
}
</#escape>