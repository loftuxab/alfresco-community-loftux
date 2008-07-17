<div class="dashlet">
  <div class="title">${msg("header.myLimitedProfile")}</div>
  <div class="toolbar">
     <a href="${url.context}/page/user-profile">${msg("link.viewFullProfile")}</a>
  </div>
  <div class="body">
     <h3 style="padding:2px">${user.properties["firstName"]} ${user.properties["lastName"]}, Welcome</h3>
     <h4 style="padding:2px">${msg("label.organization")} ${user.properties["organization"]!""}</h4>
     <h4 style="padding:2px">${msg("label.jobTitle")} ${user.properties["jobtitle"]!""}</h4>
     <h4 style="padding:2px">${msg("label.location")} ${user.properties["location"]!""}</h4>
  </div>
</div>