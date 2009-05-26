<div id="container">
   <div id="${htmlid}Panel" class="panel selected">
     <div class="toolbar">
      <h1>${msg('Workflow')}</h1>
      <#if (backButton??)>
         <a class="back button">${msg('label.backText')}</a>
      </#if>
      <#if (actionUrl??)>
         <a class="button action" href="${actionUrl}">${msg('label.actionText')}</a>         
      </#if>
    </div>
    <div class="content">
         <form action="${url.context}/p/documents" method="post">
           <h2>Type</h2>
           <select name="type">
             <option value="wf:review">${msg('Review and Approve')}</option>
             <option value="wf:adhoc">${msg('Adhoc')}</option>             
           </select>
           <h2>${msg('Description')}</h2>
           <textarea name="description" rows="4" cols="40"></textarea>
           <h2>${msg('User')}</h2>
           <input type="text" value="" name="user" class="typeAhead"/>
           <h2>${msg('Due Date')}</h2>
           <input name="datePicker" id="datePicker" value="${msg('Pick Due Date')}" type="button" class="datepicker"/>
           <div>
             <input type="button" value="${msg('Cancel')}" class="button">            
             <input type="submit" value="${msg('Assign')}" class="button">
           </div>
           <input type="hidden" name="nodeRef" value="${page.url.args.nodeRef}"/>
           <input type="hidden" name="site" value="${page.url.args.site}"/>
           <input type="hidden" name="date" value="" id="date">           
         </form>
    </div>
   </div>
</div>