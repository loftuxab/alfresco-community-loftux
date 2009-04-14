<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
     <div class="toolbar">
      <h1>${pageTitle}</h1>
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
              <li><a href="#Fav" class="button active">${msg('Favourites')}</a></li>
              <li><a href="#My" class="button">${msg('My Sites')}</a></li>
              <li><a href="#All" class="button">${msg('All')}</a></li>
            </ul> 
            <div class="tabcontent">
              <ul id="Fav" class="e2e list active fav">
                <#list favSites as site>
                <li><a id="${site.shortName}" href="#site?site=${site.shortName}" class="panelLink">${site.shortName}</a></li>
                </#list>
                <#-- <li><a id="Sales" href="#site.html" class="panelLink">Sales</a></li>
                <li><a id="Project-Mobile" href="#site.html" class="panelLink">Project Mobile</a></li>
                <li><a id="New-Wiki" href="#newwikipage.html" class="panelLink">Project Jaws</a></li> -->
              </ul>
              <ul id="My" class="e2e list">
                <#list sites as site>
                <li><a id="${site.shortName}" href="#site?site=${site.shortName}" class="panelLink">${site.shortName}</a></li>
                </#list>                
                <#-- <li><a id="My-Site-1" href="#site.html" class="panelLink">My Site 1</a></li>
                <li><a id="My-Site-2" href="#site.html" class="panelLink">My Site 2</a></li> -->
              </ul>
              <ul id="All" class="e2e list">
                <#list allSites as site>
                <li><a id="${site.shortName}" href="#site?site=${site.shortName}" class="panelLink">${site.shortName}</a></li>
                </#list>                
                <#-- <li><a id="My-Site-1" href="#site.html" class="panelLink">My Site 1</a></li>
                <li><a id="My-Site-2" href="#site.html" class="panelLink">My Site 2</a></li>
                <li><a id="Project-Mobile" href="#site.html" class="panelLink">Project Mobile</a></li>
                <li><a id="Project-Jaws" href="#site.html" class="panelLink">Project Jaws</a></li>
                <li><a id="Sales" href="#site.html" class="panelLink">Sales</a></li> -->
              </ul>
            </div>
          </div>
    </div>
   </div>
</div>