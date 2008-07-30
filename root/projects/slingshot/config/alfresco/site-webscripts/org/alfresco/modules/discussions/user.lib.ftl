
<#--
   User related rendering macros.
   A user object is expected of following form:
   
   {
      "username" : "name of the user",
      "firstName" : "first name",
      "lastName" : "last name",
      "avatarRef" : node reference of the avatar image
   }
   
   firstName, lastName and avatarRef can be missing, in which case
   the macros will do a fallback to defaults
-->


<#macro renderAvatarImage user>
   <#if user.avatarRef??>
      <#assign avatarUrl>${url.context}/proxy/alfresco/api/node/${user.avatarRef?replace('://','/')}/content/thumbnails/avatar?qc=true&amp;ph=true</#assign>
   <#else>
      <#assign avatarUrl="${url.context}/components/images/no-user-photo-64.png" />
   </#if>
   <img src="${avatarUrl}" alt="${user.username}-avatar-image" />
</#macro>


<#--
   Renders user name information/link
   A user structure should be passed with the following information
   {  username, firstName, lastName, avatarRef  }
-->
<#macro renderUserLink user>
   <a href="${url.context}/page/user/${user.username}/profile"><@renderUserName user=user /></a>
</#macro>

<#--
   Renders a user name using the first/last name if available
-->
<#macro renderUserName user>
   <#if ((user.firstName?? && user.firstName?length > 0) || (user.lastName?? && user.lastName?length > 0))>
      ${user.firstName!''} ${user.lastName!''}
   <#else>
      ${user.username}
   </#if>
</#macro>
