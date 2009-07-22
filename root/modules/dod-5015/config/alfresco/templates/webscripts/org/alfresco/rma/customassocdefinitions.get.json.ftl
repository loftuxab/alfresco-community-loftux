<#escape x as jsonUtils.encodeJSONString(x)>
{
	"data":
	{
		"customAssociations":
		{
			<#list customAssocs as assoc>
			"${assoc.name}":
			{
				"isChildAssociation": ${assoc.childAssociation?string},
				"title": "${assoc.title!""}",
				"description": "${assoc.description!""}",
				"sourceRoleName": "${assoc.sourceRoleName!""}",
				"sourceMandatory": ${assoc.sourceMandatory?string},
				"sourceMany": ${assoc.sourceMany?string},
				"targetRoleName": "${assoc.targetRoleName!""}",
				"targetMandatory": ${assoc.targetMandatory?string},
				"targetMandatoryEnforced": ${assoc.targetMandatoryEnforced?string},
				"targetMany": ${assoc.targetMany?string},
				"protected": ${assoc.protected?string}
			}<#if assoc_has_next>,</#if>
			</#list>
		}
	}
}
</#escape>