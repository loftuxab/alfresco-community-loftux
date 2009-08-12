<#escape x as jsonUtils.encodeJSONString(x)>
{
	"data":
	{
		"customReferences":
		{
			<#list customRefs as ref>
			"${ref.name.toPrefixString()}":
			{
				"title": "${ref.title!""}",
				"description": "${ref.description!""}",
				"isChild": ${ref.child?string},
                "sourceRoleName": "<#if ref.sourceRoleName??>${ref.sourceRoleName.toPrefixString()}</#if>",
                "targetRoleName": "<#if ref.targetRoleName??>${ref.targetRoleName.toPrefixString()}</#if>",
                "isTargetMandatory": ${ref.targetMandatory?string},
				"protected": ${ref.protected?string}
			}<#if ref_has_next>,</#if>
			</#list>
		}
	}
}
</#escape>