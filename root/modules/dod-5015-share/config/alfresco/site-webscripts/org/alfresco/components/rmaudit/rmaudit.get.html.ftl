<script type="text/javascript" charset="utf-8">    
    new Alfresco.RM_Audit('audit').setOptions(
       {
          'viewMode':Alfresco.RM_Audit.VIEW_MODE_COMPACT,
          'enabled' : ${auditStatus.enabled?string},
          'startDate':"${auditStatus.started}",
          'stopDate':"${auditStatus.stopped}",
          'pollInterval':5000          
       }
    ).setMessages(${messages});
  </script>
<div id="audit">
 <div class="yui-gc">
   	<div class="yui-u first">
          <div id="audit-info">
            <h1>${msg("label.title")}</h1>
            <p id="audit-from-date">${msg('label.from')}</p>
            <p id="audit-to-date">${msg('label.to')}</p>
          </div>
   	</div>
    </div>    
    <div id="audit-log" class="yui-gc">
   	<div class="yui-u first">
         <div id="defaultActions" class="active">
            <label for="audit-specifyfilter">${msg("label.show-log-for")}</label> <div id="personFilter"><span>${msg("label.all-users")}</span><a id="personFilterRemove"><img src="${page.url.context}/components/images/remove-icon-16.png"  alt="${msg('label.remove-filter')}"/></a></div>
               <button id="audit-specifyfilter" name="audit-specifyfilter">${msg("label.button-specify")}</button>
         </div>
         <div id="audit-peoplefinder">
         </div>
         <div id="auditDT">
         </div>    
      </div>
    </div>
</div>
