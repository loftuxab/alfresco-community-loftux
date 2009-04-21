<div id="container">
   <div id="homePanel" class="panel selected">
      <div class="toolbar">
         <h1>${pageTitle}</h1>
      </div>
      <div class="content">
         <ul class="nav list">
            <li>
              <h2>${msg('My Alfresco Share')}</h2>
              <ul id="my" class="rr hilite">
                <li class="recentdocs"><a id="Recent-Documents"  class="disabled">${msg('My Recent Documents')}</a></li>
                <li class="recentactivity"><a id="Recent-Activity" class="disabled">${msg('My Recent Activity')}</a></li>
                <li class="fav"><a id="Favourite-Sites" href="#sites" class="panelLink">${msg('My Favourite Sites')}</a></li>
                <li class="sites"><a class="disabled">${msg('My Sites')}</a></li>
              </ul>
            </li>
            <li>
              <h2>${msg('Today')}</h2>
              <ul class="rr hilite">
                <li class="tasks"><a id="Tasks" href="#tasks" class="panelLink disabled">${msg('Tasks ')} <span>(${numTasks})</span></a></li>
                <li class="events"><a class="disabled">${msg('Events')} <span>(${numEvents})</span></a></li>
              </ul>
            </li>
            <li>
              <ul class="rr hilite">
                 <li class="siteactivities"><a class="disabled">${msg('Site Activities')}</a></li>
              </ul>
            </li>
            <li>
              <ul class="rr hilite">
                <li class="all"><a class="disabled">${msg('All Tasks')}</a></li>
              </ul>
            </li>
            <li>
              <ul class="rr hilite">
                 <li class="publicsites"><a class="disabled">${msg('Browse Public Sites')}</a></li>
              </ul>
            </li>  
            <li>
              <h2>${msg('Quick Actions')}</h2>
              <ul class="rr hilite">            
                 <li class="newwikipage"><a class="panelLink disabled" href="sites">${msg('New Wiki Page')}</a></li>
                 <li class="invitetosite"><a href="#selectsite.html" class="panelLink disabled">${msg('Invite to Site')}</a></li>
              </ul>
            </li>
         </ul>
      </div>
   </div>
</div>