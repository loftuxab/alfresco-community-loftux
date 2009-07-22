<#escape x as jsonUtils.encodeJSONString(x)>
{
	"data":
	{
		"customProperties":
		{
			<#list customProps as prop>
			"${prop.name}":
			{
				"type": "${prop.type!""}",
				"title": "${prop.title!""}",
				"description": "${prop.description!""}",
				"mandatory": ${prop.mandatory?string},
				"multiValued": ${prop.multiValued?string},
				"defaultValue": "${prop.defaultValue!""}",
				"protected": ${prop.protected?string}
			}<#if prop_has_next>,</#if>
			</#list>
		}
	}
}
</#escape>