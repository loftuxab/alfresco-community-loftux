<!-- Main gui -->

<div class="component">
  <div class="component-title">My Limited Profile</div>
  <div class="component-links">
    <a href="#" onclick="alert('Not implemented');">Edit profile ></a>
    <a href="#" onclick="alert('Not implemented');">View Full Profile ></a>
  </div>
  <div class="component-body">
     <div class="header">${profile.name}, Welcome</div>
     <div class="text">${profile.title}</div>
     <div class="text">${profile.department}, ${profile.company}</div>
     <div class="text">Based in ${profile.location}</div>
     <#if profile.status == 'ONLINE'>
     <div class="text">Online, ${profile.loggedIn}</div>
     <#else>
     <div class="text">Offline</div>
     </#if>
     <div class="component-text-divider"></div>
     <div class="text">${profile.email}</div>
     <div class="text">${profile.mobile}</div>
     <div class="text">${profile.skype}</div>
     <div class="text">${profile.linkedIn}</div>
  </div>
</div>
