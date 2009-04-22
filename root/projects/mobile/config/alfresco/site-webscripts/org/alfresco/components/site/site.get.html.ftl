<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
      <div class="toolbar">
         <h1>${msg('label.site')}</h1>
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
               <p>${site.description?html}</p>
            </div>
            <a href="#TODO" class="more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
         </div>
         <ul class="nav list">
           <li>
             <h2>${msg('label.documentLibrary')}</h2>
             <ul class="rr">
               <li class="documents"><a id="Documents" href="#documents?site=${page.url.args.site}#RMod" class="panelLink">${msg('label.recentlyModified')}</a></li>
               <li class="mydocuments"><a id="My-Documents" href="#documents?site=${page.url.args.site}#My" class="disabled panelLink">${msg('label.myDocuments')}</a></li>
               <li class="alldocuments"><a id="All-Documents" href="#documents?site=${page.url.args.site}#All" class="disabled panelLink" >${msg('label.allDocuments')}</a></li>
               <li class="allfolders"><a id="All-Folders" class="disabled">${msg('label.allFolders')}</a></li>
             </ul>
           </li>
           <li>
             <h2>${msg('label.wiki')}</h2>
             <ul id="my" class="rr">
               <li class="mainwikipage"><a id="Main-Page" href="#wiki" class="panelLink disabled">${msg('label.mainPage')}</a></li>
               <li class="wikipagelist"><a id="Wiki-Page-List" class="disabled">${msg('label.wikiPageList')}</a></li>
               <li class="newwikipage"><a id="New-Wiki-Page" class="panelLink disabled" href="newwikipage">${msg('label.newWikiPage')}</a></li>
             </ul>
           </li>
           <li>
             <h2>${msg('label.siteActions')}</h2>
             <ul id="my" class="rr">
               <li class="invitetosite"><a id="Invite-To-Site" class="disabled">${msg('label.inviteToSite')}</a></li>
               <li class="leavesite"><a id="Leave-Site" class="disabled">${msg('label.leaveSite')}</a></li>
             </ul>
           </li>
         </ul>
      </div>
   </div>
</div>