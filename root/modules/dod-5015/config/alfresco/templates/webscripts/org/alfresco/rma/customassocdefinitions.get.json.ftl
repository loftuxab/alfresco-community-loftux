<#import "customassociation.lib.ftl" as customAssociationLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
	"data":
	{
		"customAssociations":
		{
			<#list customAssocs as assoc>
			"${assoc.name}":
			{
                <@customAssociationLib.customAssociationJSON association=assoc/>
			}<#if assoc_has_next>,</#if>
			</#list>
		}
	}
}
</#escape>