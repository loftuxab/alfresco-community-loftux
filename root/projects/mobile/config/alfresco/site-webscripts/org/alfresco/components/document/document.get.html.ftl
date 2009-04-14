<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
     <div class="toolbar">
      <h1>${doc.title}</h1>
      <#if (backButton??)>
         <a class="back button">${msg('label.backText')}</a>
      </#if>
      <#if (actionUrl??)>
         <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>         
      </#if>
    </div>
    <div class="content">
       <div class="panelBar">
            <a class="button" href="workflow.html">${msg('Assign Workflow')}</a>
            <a class="button delete">${msg('Delete')}</a><!-- make form button-->
          </div>
          <a class="preview" href="/share/proxy/alfresco-feed/${doc.contentUrl}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png" /></a>
          <ul class="rr info">
              <li><span>${msg('Name')}:</span> ${doc.title}</li>
              <li><span>${msg('Description')}:</span> ${doc.description}</li>
              <li><span>${msg('Size')}:</span> ${doc.size} Kb</li> <!-- todo kb text fix-->
              <li><span>${msg('Type')}:</span> ${doc.type}</li>
              <li><span>${msg('Tags')}:</span> ${doc.tags}</li>              
          </ul>
      </div>
   </div>
</div>