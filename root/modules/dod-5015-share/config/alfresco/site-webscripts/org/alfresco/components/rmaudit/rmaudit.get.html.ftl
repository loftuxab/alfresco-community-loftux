<script type="text/javascript" charset="utf-8">    
    new Alfresco.RM_Audit('${htmlid}-audit').setOptions(
       {
          'siteId': "${page.url.templateArgs.site!"rm"}",
          'containerId': "${template.properties.container!"documentLibrary"}",          
          'viewMode':Alfresco.RM_Audit.VIEW_MODE_COMPACT,
          'enabled' : ${auditStatus.enabled?string},
          'startDate':"${auditStatus.started}",
          'stopDate':"${auditStatus.stopped}"
          <#if (nodeRef?exists)>
            ,'nodeRef' : '${nodeRef}'
          </#if>          
       }
    ).setMessages(${messages});
  </script>
<div id="${htmlid}-audit" class="audit">
 <div class="yui-gd">
   	<div class="yui-u first">
          <div id="${htmlid}-audit-info" class="audit-info">
            <h1>${msg("label.title")}</h1>
            <p id="${htmlid}-audit-from-date" class="audit-from-date">${msg('label.from')}</p>
            <p id="${htmlid}-audit-to-date" class="audit-to-date">${msg('label.to')}</p>
          </div>
   	</div>
	   <div class="yui-u">
		   <div id="${htmlid}-auditActions" class="auditActions">
            <button id="${htmlid}-audit-export" name="audit-export" class="audit-export">${msg("label.button-export")}</button>
            <button id="${htmlid}-audit-declare-record" name="audit-declare-record" class="audit-declare-record">${msg("label.button-declare-record")}</button>            
         </div>
	   </div>   	
    </div>    
    <div id="${htmlid}-audit-log" class="yui-gc audit-log">
   	<div class="yui-u">
         <div id="${htmlid}-defaultActions" class="active defaultActions">
            <label for="audit-specifyfilter">${msg("label.show-log-for")}</label> <div id="${htmlid}-audit-personFilter" class="personFilter"><span>${msg("label.all-users")}</span><a id="${htmlid}-personFilterRemove" class="personFilterRemove"><img src="${page.url.context}/components/images/remove-icon-16.png"  alt="${msg('label.remove-filter')}"/></a></div>
               <button id="${htmlid}-audit-specifyfilter" name="audit-specifyfilter" class="audit-specifyfilter">${msg("label.button-specify")}</button>
         </div>
         <div id="${htmlid}-audit-peoplefinder" class="audit-peoplefinder">
         </div>
         <div id="${htmlid}-audit-auditDT" class="auditDT">
         </div>    
      </div>
    </div>
    <div id="${htmlid}-copyMoveFileTo"></div>
</div>
