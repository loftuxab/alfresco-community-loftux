<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <div class="toolbar">
         <h1>${msg('label.profile')}</h1>
         <#if (backButton??)>
         <a class="back button">${msg('label.backText')}</a>
         </#if>
         <#if (actionUrl??)>
         <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>
         </#if>
      </div>
      <div class="content">   
         <div class="tabs">
            <ul class="tablinks">
              <li><a href="#Info" class="button active">${msg('label.information')}</a></li>
              <li><a href="#Sites" class="button">${msg('label.sites')}</a></li>
            </ul> 
            <div class="tabcontent">

              <div id="Info" class="active">
                <div class="details">
                  <img src="${url.context}/themes/${theme}/images/64-siteicon.png" width="64" height="64" >
                  <div>
                     ${profile.name!""}
                    <h2>${profile.firstName!""} ${profile.lastName!""}</h2>
                    <p>${profile.jobTitle!""}</p>
                    <p>${profile.location!""}</p>
                    <p>${profile.organization!""}</p>
                  </div>
                </div>
                <h2>${msg('Contact Information')}</h2>
                <ul class="e2e list">
                  <li>
                    <span>${msg('Telephone')}:</span> <a href="tel:${profile.telephone!""}">${profile.telephone!""}</a>
                  </li>
                  <li>
                    <span>${msg('Mobile')}:</span> <a href="tel:${profile.mobilePhone!""}">${profile.mobilePhone!""}</a>
                  </li>
                  <li>
                    <span>${msg('Email')}:</span> <a href="mailto:${profile.email!""}">${profile.email!""}</a>
                  </li>
                  <li>
                    <span>${msg('Skype')}:</span> <a href="#skype:${profile.skype!""}">${profile.skype!""}</a>
                  </li>
                </ul>
                <h2>${msg('Company Details')}</h2>
                <ul class="e2e list">
                  <li>
                    <span>${msg('Name')}:</span> <a href="#">${profile.organization!""}</a>
                  </li>
                </ul>                
              </div>
              <#if ((numUserSites?number>0))>
              <ul id="Sites" class="e2e list hilite">
                <#list userSites.sites as site>
                <li><a id="${site.shortName}" href="#site?site=${site.shortName}" class="panelLink">${site.shortName}</a></li>
                </#list>
              </ul>
              <#else>
              <p id="Sites">${msg('label.noMembership')}</p>
              </#if>
            </div>
          </div>
      </div>
   </div>
</div>