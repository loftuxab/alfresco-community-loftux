<div class="dashlet">
  <div class="title">${msg("title")}</div>
  <div class="toolbar">
     <a href="${url.context}/page/user-profile">${msg("viewFullProfile")}</a>
  </div>
  <div class="body">
     <h3 style="padding:2px">${user.properties["first_name"]} ${user.properties["last_name"]}, Welcome</h4>
     <h4 style="padding:2px">${msg("organization")} ${user.properties["organization"]!""}</h4>
     <h4 style="padding:2px">${msg("jobTitle")} ${user.properties["job_title"]!""}</h4>
     <h4 style="padding:2px">${msg("location")} ${user.properties["location"]!""}</h4>
  </div>
</div>