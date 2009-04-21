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
              <li><a href="#Fav" class="button active">${msg('label.favorites')}</a></li>
              <li><a href="#My" class="button">${msg('label.mySites')}</a></li>
              <li><a href="#All" class="button">${msg('label.all')}</a></li>
            </ul>
            <div class="tabcontent">
              <ul id="Fav" class="e2e list active hilite">
                <#list favSites as site>
                <li class="fav"><a id="${site.shortName}" href="#site?site=${site.shortName}" class="panelLink">${site.shortName}</a></li>
                </#list>
              </ul>
              <ul id="My" class="e2e list hilite">
                <#list sites as site>
                <li><a id="${site.shortName}" href="#site?site=${site.shortName}" class="panelLink">${site.shortName}</a></li>
                </#list>
              </ul>
              <ul id="All" class="e2e list hilite">
                <#list allSites as site>
                <li><a id="${site.shortName}" href="#site?site=${site.shortName}" class="panelLink">${site.shortName}</a></li>
                </#list>
              </ul>
            </div>
         </div>
      </div>
   </div>
</div>