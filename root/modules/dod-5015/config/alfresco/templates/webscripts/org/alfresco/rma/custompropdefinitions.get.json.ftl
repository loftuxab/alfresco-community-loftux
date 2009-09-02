<#escape x as jsonUtils.encodeJSONString(x)>
{
	"data":
	{
		"customProperties":
		{
			<#list customProps as prop>
			"${prop.name.toPrefixString()}":
			{
				"dataType": "<#if prop.dataType??>${prop.dataType.name.toPrefixString()}</#if>",
				"title": "${prop.title!""}",
				"description": "${prop.description!""}",
				"mandatory": ${prop.mandatory?string},
				"multiValued": ${prop.multiValued?string},
				"defaultValue": "${prop.defaultValue!""}",
				"protected": ${prop.protected?string},
				"propId": "${prop.name.localName}"
			}<#if prop_has_next>,</#if>
			</#list>
		}
	}
}
</#escape>