<div class="dashlet">
   <div class="title">${msg("header.myLimitedProfile")}</div>
   <div class="toolbar">
      <a href="${url.context}/page/user-profile">${msg("link.viewFullProfile")}</a>
   </div>
   <div class="body">
      <table cellspacing="0" cellpadding="0" border="0" width="100%">
         <tr>
            <td>
               <h3 style="padding:2px">${user.properties["firstName"]} ${user.properties["lastName"]}, Welcome</h3>
               <h4 style="padding:2px">${msg("label.organization")}: ${user.properties["organization"]!""}</h4>
               <h4 style="padding:2px">${msg("label.jobTitle")}: ${user.properties["jobtitle"]!""}</h4>
               <h4 style="padding:2px">${msg("label.location")}: ${user.properties["location"]!""}</h4>
            </td>
            <#if user.properties.avatar??>
            <td style="width:64px;padding-left:8px" valign="middle">
               <img src="${url.context}/proxy/alfresco/api/node/${user.properties.avatar?replace('://','/')}/content/thumbnails/avatar?c=force" alt="" />
            </td>
            </#if>
         </tr>
      </table>
   </div>
</div>