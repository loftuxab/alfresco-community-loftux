<div class="dashlet">
   <div class="title">${msg("header.myLimitedProfile")}</div>
   <div class="toolbar">
      <a href="${url.context}/page/user/${user.name?url}/profile">${msg("link.viewFullProfile")}</a>
   </div>
   <div class="body profile">
      
      <div class="photorow">
         <div class="photo">
            <#if user.properties.avatar??>
               <img  class="photoimg" src="${url.context}/proxy/alfresco/api/node/${user.properties.avatar?replace('://','/')}/content/thumbnails/avatar?c=force" alt="" />
            <#else>
               <img class="photoimg" src="${url.context}/components/images/no-user-photo-64.png" alt="" />
            </#if>
         </div>
         <div class="namelabel">${user.properties["firstName"]!""} ${user.properties["lastName"]!""}</div>
         <div class="fieldlabel">${user.properties["jobtitle"]!""}</div>
      </div>
      <hr class="clear"/>
      <div class="row">
         <div class="fieldlabelright">${msg("label.email")}:</div>
         <div class="fieldvalue"><#if user.properties["email"]??><a href="mailto:${user.properties["email"]!""}">${user.properties["email"]}</a></#if></div>
      </div>
      <div class="row">
         <div class="fieldlabelright">${msg("label.phone")}:</div>
         <div class="fieldvalue">${user.properties["telephone"]!""}</div>
      </div>
      <div class="row">
         <div class="fieldlabelright">${msg("label.skype")}:</div>
         <div class="fieldvalue">${user.properties["skype"]!""}</div>
      </div>
      <div class="row">
         <div class="fieldlabelright">${msg("label.msn")}:</div>
         <div class="fieldvalue">${user.properties["msn"]!""}</div>
      </div>   
    
   </div>
</div>