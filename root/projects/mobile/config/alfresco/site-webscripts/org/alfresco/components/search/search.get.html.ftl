<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
     <div class="toolbar">
      <h1>${msg('SearchResults')}</h1>
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
             <li><a href="#Content" class="button active">Content</a></li>
             <li><a href="#Sites" class="button">Sites</a></li>
             <li><a href="#People" class="button">People</a></li>
           </ul>
            
           <div class="tabcontent">
             <h2>TODO - 98 results in All sites <span>25 of 98</span></h2>
             <ul id="Content" class="e2e list active">
                <#list contentResults.items as content>
                <li class="details ${content.type}">
                 <p class="toenail"><a href="${url.context}/proxy/alfresco/${content.browseUrl}"><img src="${url.context}/themes/${theme}/images/icons/${content.displayType}.png" /></a></p>
                 <h3><a href="${url.context}/proxy/alfresco/${content.browseUrl}">${content.displayName}</a></h3>
                 <p><span>${msg('label.modifiedBy')}:</span> ${content.modifiedBy}</p>
                 <p><span>${msg('label.modifiedOn')}:</span> ${content.modifiedOn?string("dd MMM yyyy HH:mm")}</p>
                 <p><span>${msg('label.size')}:</span> ${content.size}</p>
                 <a id="Mobile-User-Stories" href="#docactions.html" class="panelLink more"><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
               </li>
                </#list>
               </ul>
               <ul id="Sites" class="e2e list">
               <#list siteResults as site>
               <li class="details">
                 <p class="toenail"><a href="${url.context}/p/site?site=${site.shortName}"><img src="${url.context}/themes/${theme}/images/64-siteicon.png"/></a></p>
                 <h3><a href="${url.context}/p/site?site=${site.shortName}">${site.title}</a></h3>
                 <p>${site.description}</p>
                 <a href="${url.context}/p/site?site=${site.shortName}" class="panelLink more" ><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
               </li>
               </#list>
               </ul>
               <ul id="People" class="e2e list">
               <#list pplResults.people as person>
               <li class="details">
                  <#if (person.avatar??)>
                    <p class="toenail"><a href="${url.context}/p/profile?person=${person.userName}"><img src="${url.context}/proxy/alfresco/${person.avatar}"/></a></p>
                  <#else>
                    <p class="toenail"><a href="${url.context}/p/profile?person=${person.userName}"><img src="${url.context}/themes/${theme}/images/no-user-photo-64.png"/></a></p>
                  </#if>
                 <a href="#userprofile.html" class="panelLink"><p><span>${msg('Name')}:</span> ${person.firstName} ${person.lastName}</p>
                 <p><span>${msg('Title')}</span> ${person.jobtitle!''}:</p></a>
                 <a href="${url.context}/p/profile?person=${person.userName}" class="panelLink more" ><img src="${url.context}/themes/${theme}/images/30-goarrow2.png" /></a>
               </li>
               </#list>
             </ul>
             <a id="loadmore" class="button white">Load next 25 results</a>
           </div>
         </div>
    </div>
   </div>
</div>