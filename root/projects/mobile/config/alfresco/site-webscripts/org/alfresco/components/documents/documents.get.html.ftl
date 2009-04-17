<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
     <div class="toolbar">
      <h1>${msg('Documents')}</h1>
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
             <li><a href="#My" class="button">${msg('My')}</a></li>
             <li><a href="#RMod" class="button active">${msg('Recently Modified<')}/a></li>
             <li><a href="#All" class="button">${msg('All')}</a></li>
           </ul>
           <div class="tabcontent">
             <div id="RMod" class="active">
             <ul class="list">
               <li>
                 <h2>${msg('Today')}</h2>
                 <ul class="e2e">
                   <#list recentDocs as doc >
                   <li class="details ${doc.type}">
                      <p class="toenail"><a href="/share/proxy/alfresco-feed/${doc.contentUrl}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png"/></a></p>                  
                     <h3><a href="/share/proxy/alfresco-feed/${doc.contentUrl}">${doc.title}</a></h3>
                     <p><span>${msg('Modified by')}:</span> ${doc.modifiedBy}</p>
                     <p><span>${msg('Modified on')}:</span> ${doc.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
                     <p><span>${msg('Size')}:</span> ${doc.size} kb</p>
                     <a id="${doc.domId}" href="#document?nodeRef=${doc.nodeRef}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
                  </li>
                   </#list>
                   <#-- <li class="details doc">
                     <h3><a href="../testfiles/test.pdf">Mobile User Stories.doc</a></h3>
                     <p><span>${msg('Modified by')}:</span> Kurt Damewood</p>
                     <p><span>${msg('Modified on')}:</span> 25th Feb 2009 10:12</p>
                     <p><span>${msg('Size')}:</span> 121KB</p>
                     <a id="Mobile-User-Stories" href="#document.html" class="panelLink more"><img src="static/images/30-goarrow2.png" /></a> 
                   </li> -->
                 </ul>
               </li>
               <li>
                 <h2>${msg('Yesterday')}</h2>
                 <ul>
                   <#-- TODO <li class="details pdf">
                     <h3><a href="../testfiles/test.pdf">Mobile User Stories.doc</a></h3>
                     <p><span>${msg('Modified by')}:</span> Kurt Damewood</p>
                     <p><span>${msg('Modified on')}:</span> 25th Feb 2009 10:12</p>
                     <p><span>${msg('Size')}:</span> 121KB</p>
                     <a id="Mobile-User-Stories" href="#document.html" class="panelLink more"><img src="static/images/30-goarrow2.png" /></a>
                   </li> -->
                 </ul>
               </li>
             </ul>
            </div>
            <div id="My">
             <ul class="e2e list">
                   <#list myDocs as doc >
                   <li class="details ${doc.type}">
                      <p class="toenail"><a href="/share/proxy/alfresco-feed/${doc.contentUrl}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png"/></a></p>
                     <h3><a href="/share/proxy/alfresco-feed/${doc.contentUrl}">${doc.title}</a></h3>
                     <p><span>${msg('Modified by')}:</span> ${doc.modifiedBy}</p>
                     <p><span>${msg('Modified on')}:</span> ${doc.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
                     <p><span>${msg('Size')}:</span> ${doc.size} kb</p>
                     <a id="${doc.domId}" href="#document?nodeRef=${doc.nodeRef}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
                  </li>
                  </#list>
             </ul>
            </div>
            <div id="All">
             <ul class="e2e list">
               <#-- <li class="details ppt"> -->
                   <#list allDocs as doc >
                   <li class="details ${doc.type}">
                      <p class="toenail"><a href="/share/proxy/alfresco-feed/${doc.contentUrl}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png"/></a></p>
                     <h3><a href="/share/proxy/alfresco-feed/${doc.contentUrl}">${doc.title}</a></h3>
                     <p><span>${msg('Modified by')}:</span> ${doc.modifiedBy}</p>
                     <p><span>${msg('Modified on')}:</span> ${doc.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
                     <p><span>${msg('Size')}:</span> ${doc.size} kb</p>
                     <a id="${doc.domId}" href="#document?nodeRef=${doc.nodeRef}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
                  </li>
                  </#list>
                 
             </ul>
            </div>
             <ul class="rr list">
               <li class="allfolders"><a class="disabled">${msg('All Folders')}</a></li>
             </ul>
           </div>
         </div>
      </div>
   </div>
</div>