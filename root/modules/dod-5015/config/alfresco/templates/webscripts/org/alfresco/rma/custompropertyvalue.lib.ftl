<#macro dateFormat date>${date?string("dd MMM yyyy HH:mm:ss 'GMT'Z '('zzz')'")}</#macro>
<#macro customPropertyJSON property>
<#escape x as jsonUtils.encodeJSONString(x)>
                "${property.qname}":
                <#if property.value??>
					<#assign val = property.value>
                    <#if val?is_date>"${xmldate(val)}"
                    <#elseif val?is_boolean>${val?string(true, false)}
					<#elseif val?is_enumerable>[<#list val as p>"${p}"<#if p_has_next>, </#if></#list>]
					<#elseif val?is_number>${val?c}
					<#else>"${val}"
					</#if><#else>""</#if>
</#escape>
</#macro>