<script type="text/javascript" charset="utf-8">    
    new Alfresco.RM_Audit('${htmlid}-audit').setOptions({
       'siteId': "${page.url.templateArgs.site!"rm"}",
       'containerId': "${template.properties.container!"documentLibrary"}",
       'viewMode':Alfresco.RM_Audit.VIEW_MODE_DEFAULT,
       'auditEvents': ${eventsStr}
    }).setMessages(${messages});
  </script>
  
  <div id="${htmlid}-audit" class="audit">
    <div class="yui-gc">
   	<div class="yui-u first">
          <div id="${htmlid}-audit-info" class="audit-info">
             <h2>${msg("label.title")}</h2>
             <p id="${htmlid}-audit-status-date" class="audit-status-date"></p>
          </div>
   	</div>
	   <div class="yui-u">
		   <div id="${htmlid}-auditActions" class="auditActions">
            <button id="${htmlid}-audit-toggle" name="${htmlid}-audit-toggle" value="" class="audit-toggle"></button>
            <button id="${htmlid}-audit-view" name="audit-view" class="audit-view">${msg("label.button-view-log")}</button>
            <button id="${htmlid}-audit-clear" name="audit-clear" class="audit-clear">${msg("label.button-clear")}</button>
         </div>
	   </div>
    </div>    
<div id="filters">
      <div class="yui-gb">
         <div class="yui-g first">
            <!-- the first child of a Grid needs the "first" class -->
            <div class="yui-u first">
               <div id="${htmlid}-entriesFilter" class="filter">
                  <div class="hd">
                     <label for="${htmlid}-audit-entries">${msg('label.header-entries')}:</label>                     
                  </div>
                  <div class="bd">
                     <input type="text" name="${htmlid}-audit-entries" value="" id="${htmlid}-audit-entries" />
                  </div>
               </div>
            </div>   
            <div class="yui-u">
               <div id="${htmlid}-dateFilter" class="filter"> 
                  <div class="hd">
                     <label for="${htmlid}-audit-fromDate">${msg('label.header-from')}:</label>
                  </div>
                  <div class="bd">
                     <!-- from Date filter -->
                     <input type="text" name="${htmlid}-audit-fromDate" disabled value="" id="${htmlid}-audit-fromDate" />
                     <a id="${htmlid}-audit-fromDate-icon" class="datepicker-icon">
                        <img class="datepicker-icon" src="${url.context}/components/form/images/calendar.png" />
                     </a>
                     <div id="${htmlid}-audit-fromDate-cal" class="datepicker"></div>
                     <!-- to Date filter -->
                     <label for="${htmlid}-audit-toDate">${msg('label.header-to')}:</label><input type="text" disabled name="${htmlid}-audit-toDate" value="" id="${htmlid}-audit-toDate" />
                     <a id="${htmlid}-audit-toDate-icon" class="datepicker-icon">
                        <img class="datepicker-icon" src="${url.context}/components/form/images/calendar.png" />
                     </a>
                     <div id="${htmlid}-audit-toDate-cal" class="datepicker"></div>
                  </div>
               </div>
            </div>
         </div>
      
         <div class="yui-g">
            <div class="yui-u first">
               <div id="${htmlid}-eventsFilter" class="filter">
                  <div class="hd">
                     <label for="${htmlid}-events=menu">${msg('label.header-event')}:</label>                     
                  </div>
                  <div class="bd">
                     <input id="${htmlid}-audit-events" type="button" name="${htmlid}-audit-events" value="${msg("label.all")}" />
                     <select name="${htmlid}-audit-events-menu" id="${htmlid}-audit-events-menu" onchange="" size="1">
                        <option value="ALL">${msg("label.all")}</option>
                        <#list events as e>
                           <option value="${e.value}">${e.label}</option>
                        </#list>
                     </select>
                  </div>
               </div>
            </div>
            <div class="yui-u first">
               <div id="${htmlid}-propertyFilter" class="filter">
                  <div class="hd">
                     <label for="property_menu">${msg('label.header-property')}:</label>
                  </div>
                  <div class="bd">
                     <input id="${htmlid}-audit-property" type="button" name="${htmlid}-audit-property" value="All" />
                     <select name="${htmlid}-audit-property-menu" id="${htmlid}-audit-property-menu" onchange="" size="1">
                        <option value="ALL">${msg("label.all")}</option>
                        <option value="KEYWORDS">${msg("label.keywords")}</option>
                        <option value="rma:identifier">${msg("label.identifier")}</option>
                        <option value="cm:name">${msg("label.name")}</option>
                        <option value="cm:title">${msg("label.title")}</option>
                        <option value="cm:description">${msg("label.description")}</option>
                        <option value="cm:creator">${msg("label.creator")}</option>
                        <option value="cm:created">${msg("label.created")}</option>
                        <option value="cm:modifier">${msg("label.modifier")}</option>
                        <option value="cm:modified">${msg("label.modified")}</option>
                        <option value="cm:author">${msg("label.author")}</option>
                        <option value="rma:originator">${msg("label.originator")}</option>
                        <option value="rma:dateFiled">${msg("label.dateFiled")}</option>
                        <option value="rma:publicationDate">${msg("label.publicationDate")}</option>
                        <option value="rma:reviewAsOf">${msg("label.reviewDate")}</option>
                        <option value="rma:originatingOrganization">${msg("label.originatingOrganization")}</option>
                        <option value="rma:mediaType">${msg("label.mediaType")}</option>
                        <option value="rma:format">${msg("label.format")}</option>
                        <option value="rma:dateReceived">${msg("label.dateReceived")}</option>
                        <option value="rma:location">${msg("label.location")}</option>
                        <option value="rma:address">${msg("label.address")}</option>
                        <option value="rmc:supplementalMarkingList">${msg("label.supplementalMarkingList")}</option>
                        <option value="rma:recordSearchDispositionEvents">${msg("label.dispositionEvents")}</option>
                        <option value="rma:recordSearchDispositionActionName">${msg("label.dispositionActionName")}</option>
                        <option value="rma:recordSearchDispositionActionAsOf">${msg("label.dispositionActionAsOf")}</option>
                        <option value="rma:recordSearchDispositionEventsEligible">${msg("label.dispositionEventsEligible")}</option>
                        <option value="rma:recordSearchDispositionPeriod">${msg("label.dispositionPeriod")}</option>
                        <option value="rma:recordSearchVitalRecordReviewPeriod">${msg("label.vitalRecordReviewPeriod")}</option>
                        <option value="rma:recordSearchHasDispositionSchedule">${msg("label.hasDispositionSchedule")}</option>
                        <#list meta as d>
                           <option value="${d.name}">${d.title}</option>
                        </#list>
                     </select>                     
                  </div>

               </div>
            </div>
         </div>
         <div class="yui-g">
            <div class="yui-u first">
               <div id="${htmlid}-userFilter" class="filter">
                  <div class="hd">
                     <label for="specify">${msg('label.header-users')}:</label>
                  </div>
                  <div class="bd">
                     <label for="audit-specifyfilter">${msg("label.show-log-for")}</label> 
                     <div id="${htmlid}-audit-personFilter" class="personFilter"><span>${msg("label.all-users")}</span><a id="${htmlid}-personFilterRemove" class="personFilterRemove"><img src="${page.url.context}/components/images/remove-icon-16.png"  alt="${msg('label.remove-filter')}"/></a></div>
                     <button id="${htmlid}-audit-specifyfilter" name="audit-specifyfilter" class="audit-specifyfilter">${msg("label.button-specify")}</button>
                     <div id="${htmlid}-audit-peoplefinder" class="audit-peoplefinder"></div>
                  </div>
               </div>
            </div>
            <div class="yui-u first">
               <div class="filter">
                  <div class="hd">
                     <button id="${htmlid}-apply" class="audit-apply">${msg('label.button-apply')}</button>
                  </div>
               </div>
            </div>
         </div>   
      </div>
   </div>    
    <div id="${htmlid}-audit-log" class="yui-gc audit-log">
   	<div class="yui-u">
         <button id="${htmlid}-audit-export" name="audit-export" class="audit-export">${msg("label.button-export")}</button>
         <button id="${htmlid}-audit-declare-record" name="audit-declare-record" class="audit-declare-record">${msg("label.button-declare-record")}</button>            
         <div id="${htmlid}-audit-auditDT" class="auditDT">
         </div>    
      </div>
    </div>
  </div>