<#escape	x as jsonUtils.encodeJSONString(x)>
{
	"data":
	{
		"nodeRef": "${nodeRef}",
		"customProperties":
		[
			<#list properties	as	property>
			{
				"qname":	"${property.qname}",
				"value":	"${property.value}"
			}<#if	property_has_next>,</#if>
			</#list>
		]
	}
}
</#escape>