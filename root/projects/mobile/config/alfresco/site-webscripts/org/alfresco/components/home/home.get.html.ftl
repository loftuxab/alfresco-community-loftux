<div id="container">
   <div id="homePanel" class="panel selected">
      <div class="toolbar">
         <h1>${pageTitle}</h1>
      </div>
      <div class="content">
         <ul class="nav list">
            <li>
              <h2>${msg('label.myAlfresco')}</h2>
              <ul id="my" class="rr hilite">
                <li class="recentdocs"><a id="Recent-Documents"  class="disabled">${msg('label.myDocuments')}</a></li>
                <li class="recentactivity"><a id="Recent-Activity" class="disabled">${msg('label.myActivity')}</a></li>
                <li class="fav"><a id="Favourite-Sites" href="#sites#Fav" class="panelLink">${msg('label.myFavoriteSites')}</a></li>
                <li class="sites"><a id="My-Sites" href="#sites#My" class="disabled panelLink">${msg('label.mySites')}</a></li>
              </ul>
            </li>
            <li>
              <h2>${msg('label.today')}</h2>
              <ul class="rr hilite">
                <li class="tasks"><a id="Tasks" href="#tasks" class="panelLink disabled">${msg('label.tasks')} <span>(${numTasks})</span></a></li>
                <li class="events"><a class="disabled">${msg('label.events')} <span>(${numEvents})</span></a></li>
              </ul>
            </li>
            <li>
              <ul class="rr hilite">
                 <li class="siteactivities"><a class="disabled">${msg('label.siteActivities')}</a></li>
              </ul>
            </li>
            <li>
              <ul class="rr hilite">
                <li class="all"><a class="disabled">${msg('label.allTasks')}</a></li>
              </ul>
            </li>
            <li>
              <ul class="rr hilite">
                 <li class="publicsites"><a id="Public-Sites" href="#sites#All" class="disabled panelLink">${msg('label.browsePublicSites')}</a></li>
              </ul>
            </li>  
            <li>
              <h2>${msg('Quick Actions')}</h2>
              <ul class="rr hilite">            
                 <li class="newwikipage"><a class="panelLink disabled" href="sites">${msg('label.newWikiPage')}</a></li>
                 <li class="invitetosite"><a href="#selectsite.html" class="panelLink disabled">${msg('label.inviteToSite')}</a></li>
              </ul>
            </li>
         </ul>
      </div>
   </div>
</div>