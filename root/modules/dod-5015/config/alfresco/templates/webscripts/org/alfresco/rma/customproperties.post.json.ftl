<#import "custompropertyvalue.lib.ftl" as customPropertyValueLib/>
<#escape	x as jsonUtils.encodeJSONString(x)>
{
	"data":
	{
		"nodeRef": "${nodeRef}",
		"customProperties":
		{
            <#list properties	as	property>
            <@customPropertyValueLib.customPropertyJSON property=property/><#if	property_has_next>,</#if>
            </#list>
		}
	}
}
</#escape>
