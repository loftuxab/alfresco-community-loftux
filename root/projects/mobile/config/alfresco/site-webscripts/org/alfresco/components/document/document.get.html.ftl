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
            <a class="button" href="workflow.html">${msg('label.assignWorkflow')}</a>
            <a class="button delete">${msg('button.delete')}</a><!-- make form button-->
          </div>
          <a class="preview" href="${url.context}/proxy/alfresco/${doc.contentUrl}"><img src="${url.context}/themes/${theme}/images/icons/${doc.type}.png" /></a>
          <ul class="rr info">
              <li><span>${msg('label.name')}:</span> ${doc.displayName}</li>
              <li><span>${msg('label.description')}:</span> ${doc.description}</li>
              <li><span>${msg('label.size')}:</span> ${doc.size} Kb</li> <!-- todo kb text fix-->
              <li><span>${msg('label.type')}:</span> ${doc.type}</li>
              <li><span>${msg('label.tags')}:</span> ${doc.tags}</li>              
          </ul>
      </div>
   </div>
</div>