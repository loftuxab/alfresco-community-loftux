<script type="text/javascript" charset="utf-8">    
    new Alfresco.RM_Audit('audit').setOptions({'viewMode':Alfresco.RM_Audit.VIEW_MODE_DEFAULT}).setMessages(${messages});
  </script>
  
  <div id="audit">
    <div class="yui-gc">
   	<div class="yui-u first">
          <h2>${msg("label.title")}</h2>
          <h3>${msg("label.started-at", logStartDate?string("EEE, dd MMM yyyy HH:mm:ss 'GMT'"))}</h3>
   	</div>
	   <div class="yui-u">
		   <div id="auditActions">
            <button id="audit-stop" name="audit-stop">${msg("label.button-stop")}</button>
            <button id="audit-view" name="audit-view">${msg("label.button-view-log")}</button>
            <button id="audit-clear" name="audit-clear">${msg("label.button-clear")}</button>
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