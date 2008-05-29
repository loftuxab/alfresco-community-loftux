<div class="dashlet">
  <div class="title">My Limited Profile</div>
  <div class="toolbar">
     <a href="${url.context}/page/user-profile">View Full Profile</a>
  </div>
  <div class="body">
     <h3 style="padding:2px">${user.properties["first_name"]} ${user.properties["last_name"]}, Welcome</h4>
     <h4 style="padding:2px">Organization: ${user.properties["organization"]}</h4>
     <h4 style="padding:2px">Job Title: ${user.properties["job_title"]}</h4>
     <h4 style="padding:2px">Location: ${user.properties["location"]}</h4>
  </div>
</div>