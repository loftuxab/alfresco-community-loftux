<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
     <div class="toolbar">
      <h1>${msg('Site')}</h1>
      <#if (backButton??)>
         <a class="back button">${msg('label.backText')}</a>
      </#if>
      <#if (actionUrl??)>
         <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>         
      </#if>
    </div>
    <div class="content">
         <div class="rr details">
           <img src="${url.context}/themes/${theme}/images/64-siteicon.png" width="64" height="64" ><!-- replace with background img-->
           <div>
             <h2>${site.shortName}</h2>
             <p>${site.description}</p>
           </div>
           <a href="#TODO" class="more"><img src="${url.context}/themes/${theme}/images/30-goarrow.png" /></a>
         </div>
         <ul class="nav list">
           <li>
             <h2>${msg('Document Library')}</h2>
             <ul class="rr">
               <li class="documents"><a id="Documents" href="#documents?site=${page.url.args.site}" class="panelLink">${msg('Recently Modified')}</a></li>
               <li class="mydocuments"><a id="My-Folders"  class="disabled">${msg('My Documents')}</a></li>
               <li class="alldocuments"><a id="All-Documents"  class="disabled">${msg('All Documents')}</a></li>
               <li class="allfolders"><a id="All-Folders" class="disabled">${msg('All Folders')}</a></li>
             </ul>
           </li>
           <li>
             <h2>${msg('Wiki')}</h2>
             <ul id="my" class="rr">
               <li class="mainwikipage"><a id="Main-Page" href="#wiki" class="panelLink disabled">${msg('Main Page')}</a></li>
               <li class="wikipagelist"><a id="Wiki-Page-List" class="disabled">${msg('Wiki Page List')}</a></li>
               <li class="newwikipage"><a id="New-Wiki-Page" class="panelLink disabled" href="newwikipage">${msg('New Wiki Page')}</a></li>
             </ul>
           </li>
           <li>
             <h2>${msg('Site Actions')}</h2>
             <ul id="my" class="rr">
               <li class="invitetosite"><a id="Invite-To-Site" class="disabled">${msg('Invite to Site')}</a></li>
               <li class="leavesite"><a id="Leave-Site" class="disabled">${msg('Leave Site')}</a></li>
             </ul>
           </li>
         </ul>
    </div>
   </div>
</div>         