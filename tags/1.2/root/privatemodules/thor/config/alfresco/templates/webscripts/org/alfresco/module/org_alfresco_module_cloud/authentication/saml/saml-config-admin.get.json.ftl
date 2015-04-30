<#escape x as jsonUtils.encodeJSONString(x)>
{
	"ssoEnabled" : ${ssoEnabled?string},     
	"idpSsoURL" : <#if idpSsoURL??>"${idpSsoURL}"<#else>null</#if>,
	"idpSloRequestURL" : <#if idpSloRequestURL??>"${idpSloRequestURL}"<#else>null</#if>,
	"idpSloResponseURL" : <#if idpSloResponseURL??>"${idpSloResponseURL}"<#else>null</#if>,
	"autoProvisionEnabled" : ${autoProvisionEnabled?string},
	"alfrescoLoginCredentialEnabled" : ${alfrescoLoginCredentialEnabled?string},
	"entityID" : "${entityID}",
	"certificate" : <#if certificateInfo??><@certificateJSONDetails certificateInfo=certificateInfo/><#else>null</#if>	
}
</#escape>

	

<#macro certificateJSONDetails certificateInfo>
{  
   		"status" : "${certificateInfo.status}", 
   		"expiryDate" : 
   		{
     		"iso8601" : "${certificateInfo.expiryDate}"
   		}
}
</#macro> 

