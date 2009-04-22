<#macro toolbar title>
   <div class="toolbar">
      <h1>${title}</h1>
      <#if (backButton??)>
         <a class="back button">${msg('label.backText')}</a>
      </#if>
      <#if (actionUrl??)>
         <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>         
      </#if>
   </div>
</#macro>

<#macro panelContent>
<div class="tabs">
   <ul class="tablinks">
      <li><a href="#My" class="button">${msg('label.my')}</a></li>
      <li><a href="#RMod" class="button active">${msg('label.recentlyModified')}</a></li>
      <li><a href="#All" class="button">${msg('label.all')}</a></li>
   </ul>
   <div class="tabcontent">
      <div id="RMod" class="active">
         <ul class="list">
            <li>
               <h2>${msg('label.today')}</h2>
               <ul class="e2e">
                  <#list recentDocs as doc >
                  <li class="details ${doc.type}">
                     <p class="toenail"><a href="${url.context}/proxy/alfresco/${doc.contentUrl}"><img   src="${url.context}/themes/${theme}/images/icons/${doc.type}.png"/></a></p>                  
                     <h3><a href="${url.context}/proxy/alfresco/${doc.contentUrl}">${doc.displayName}</a></h3>
                     <p><span>${msg('label.modifiedBy')}:</span> ${doc.modifiedBy}</p>
                     <p><span>${msg('label.modifiedOn')}:</span> ${doc.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
                     <p><span>${msg('label.size')}:</span> ${doc.size} kb</p>
                     <a id="${doc.domId}" href="#document?nodeRef=${doc.nodeRef}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
                  </li>
                  </#list>
               </ul>
            </li>
            <li>
               <h2>${msg('Yesterday')}</h2>
               <ul>
               </ul>
            </li>
         </ul>
      </div>
      <div id="My">
         <ul class="e2e list">
            <#list myDocs as doc >
            <li class="details ${doc.type}">
               <p class="toenail"><a href="${url.context}/proxy/alfresco/${doc.contentUrl}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png"/></a></p>
               <h3><a href="${url.context}/proxy/alfresco/${doc.contentUrl}">${doc.displayName}</a></h3>
               <p><span>${msg('label.modifiedBy')}:</span> ${doc.modifiedBy}</p>
               <p><span>${msg('label.modifiedOn')}:</span> ${doc.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
               <p><span>${msg('label.size')}:</span> ${doc.size} kb</p>
               <a id="${doc.domId}" href="#document?nodeRef=${doc.nodeRef}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
            </li>
            </#list>
         </ul>
      </div>
      <div id="All">
         <ul class="e2e list">
            <#list allDocs as doc >
            <li class="details ${doc.type}">
               <p class="toenail"><a href="${url.context}/proxy/alfresco/${doc.contentUrl}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png"/></a></p>
               <h3><a href="${url.context}/proxy/alfresco/${doc.contentUrl}">${doc.displayName}</a></h3>
               <p><span>${msg('label.modifiedBy')}:</span> ${doc.modifiedBy}</p>
               <p><span>${msg('label.modifiedOn')}:</span> ${doc.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
               <p><span>${msg('label.size')}:</span> ${doc.size} kb</p>
               <a id="${doc.domId}" href="#document?nodeRef=${doc.nodeRef}" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
            </li>
            </#list>
         </ul>
      </div>
      <ul class="rr list">
         <li class="allfolders"><a class="disabled">${msg('label.allFolders')}</a></li>
      </ul>
   </div>
</div>
</#macro>