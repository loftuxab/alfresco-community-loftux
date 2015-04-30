<#--
   This template renders a cloud site invitation object
-->
<#macro cloudInvitationJSON item>
<#escape x as jsonUtils.encodeJSONString(x)>
{
   "id": "${item.id}",
   "key": "${item.key}",
   
   "startDate": "${xmldate(item.startDate)}",
   "siteShortName": "${item.siteShortName!""}",
   "siteTitle": "${item.siteTitle!""}",
   "siteTenantId": "${item.siteTenantId!""}",
   "siteTenantTitle": "${item.siteTenantTitle!""}",
   
   <#if item.response??>
   "response": "${item.response}"
   </#if>

   <#if item.inviterFirstName??>
   "inviterFirstName": "${item.inviterFirstName}",
   <#elseif item.inviterProperties["cm:firstName"]??>
   "inviterFirstName": "${item.inviterProperties["cm:firstName"]}",
   </#if>
   <#if item.inviterLastName??>
   "inviterLastName": "${item.inviterLastName}",
   <#elseif item.inviterProperties["cm:lastName"]??>
   "inviterLastName": "${item.inviterProperties["cm:lastName"]}",
   </#if>
   
   "inviteeEmail": "${item.inviteeEmail!""}",
   "inviteeRole": "${item.inviteeRole!""}",
   <#if item.inviteeAvatarNode??>
   "inviteeAvatar": "${"api/node/" + item.inviteeAvatarNode?string?replace('://','/') + "/content/thumbnails/avatar"}",
   </#if>
   
   <#if item.inviteeFirstName??>
   "inviteeFirstName": "${item.inviteeFirstName}",
   <#elseif item.inviteeProperties["cm:firstName"]??>
   "inviteeFirstName": "${item.inviteeProperties["cm:firstName"]}",
   </#if>
   <#if item.inviteeLastName??>
   "inviteeLastName": "${item.inviteeLastName}",
   <#elseif item.inviteeProperties["cm:lastName"]??>
   "inviteeLastName": "${item.inviteeProperties["cm:lastName"]}",
   </#if>
   
   <#if item.inviteeIsActivated??>
   "inviteeIsActivated": ${item.inviteeIsActivated?string},
   </#if>
   <#if item.inviteeIsMember??>
   "inviteeIsMember": ${item.inviteeIsMember?string},
   </#if>
   <#-- inviterEmail is last to ensure no trailing comma irrespective of which of the parameters are null -->
   "inviterEmail": "${item.inviterEmail!""}"
}
</#escape>
</#macro>
