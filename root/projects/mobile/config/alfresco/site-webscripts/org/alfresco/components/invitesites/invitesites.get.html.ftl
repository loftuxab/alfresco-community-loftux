<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <div class="toolbar">
         <h1>${msg('label.selectSite')}</h1>
         <#if (backButton??)>
            <a class="back button">${msg('label.backText')}</a>
         </#if>
         <#if (actionUrl??)>
            <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>
         </#if>
      </div>
      <div class="content">
        <ul id="My" class="e2e list hilite">
          <#list sites as site>
          <li class="sites"><a id="${msg('label.inviteUser')}" href="#invite?site=${site.shortName}" class="panelLink">${site.shortName}</a></li>
          </#list>
        </ul>
      </div>
   </div>
</div>