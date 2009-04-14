
<div id="container">
      <div id="homePanel" class="panel selected">
         <div class="toolbar">
            <h1>${pageTitle}</h1>
         </div>
        <div class="content">
          <ul class="nav list">
            <li>
              <h2>${msg('My Alfresco Share')}</h2>
              <ul id="my" class="rr">
                <li class="recentdocs"><a id="Recent-Documents"  class="disabled">${msg('Recent Documents')}</a></li>
                <li class="recentactivity"><a id="Recent-Activity" class="disabled">${msg('Recent Activity')}</a></li>
                <li class="fav"><a id="Favourite-Sites" href="#sites" class="panelLink">${msg('Favourite Sites')}</a></li>
                <li class="sites"><a class="disabled">${msg('Sites')}</a></li>
              </ul>
            </li>
            <li>
              <h2>${msg('Today')}</h2>
              <ul class="rr">
                <li class="tasks"><a id="Tasks" href="#tasks" class="panelLink disabled">${msg('Tasks ')} <span>(${numTasks})</span></a></li>
                <li class="events"><a class="disabled">${msg('Events')} <span>(${numEvents})</span></a></li>
              </ul>
            </li>
            <li>
              <h2>${msg('Site Activities')}</h2>
              <ul class="rr">        
                  <li class="today"><a class="disabled">${msg('Today')}</a></li>
                  <li class="recentdays"><a class="disabled">${msg('Last 7 days')}</a></li>
              </ul>
            </li>
            <li>
              <h2>${msg('Tasks')}</h2>
              <ul class="rr">        
                <li class="all"><a class="disabled">${msg('All')}</a></li>       
                <li class="overdue"><a class="disabled">${msg('Overdue')}</a></li>
              </ul>
            </li>
            <li>
              <h2>${msg('Browse Sites')}</h2>
              <ul class="rr">            
                  <li class="publicsites"><a class="disabled">${msg('All Public Sites')}</a></li>
              </ul>
            </li>  
            <li>
              <h2>${msg('Quick Actions')}</h2>
              <ul class="rr">            
                  <li class="newwikipage"><a class="panelLink disabled" href="sites">${msg('New Wiki Page')}</a></li>
                  <li class="invitetosite"><a href="#selectsite.html" class="panelLink disabled">${msg('Invite to Site')}</a></li>
              </ul>
            </li>  
          </ul>       
        </div>
       </div>
      </div>