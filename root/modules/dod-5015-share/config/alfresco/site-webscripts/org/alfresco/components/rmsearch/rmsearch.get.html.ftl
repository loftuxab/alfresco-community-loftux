<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsSearch("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      customFields: YAHOO.lang.JSON.parse('[<#list meta as d>{"id": "${d.name?substring(4)}", "title": "${d.title?js_string}", "datatype": "${d.dataType}"}<#if d_has_next>,</#if></#list>]')
   }).setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="search">
   <div class="yui-g" id="${el}-header">
      <div class="yui-u first">
         <div class="title">${msg("label.searchtitle")}</div>
      </div>
      <div class="yui-u topmargin">
         <!-- New Search button -->
         <div class="right-button">
            <span class="yui-button yui-push-button" id="${el}-newsearch-button">
               <span class="first-child"><button>${msg("button.newsearch")}</button></span>
            </span>
         </div>
         
         <!-- Save Search button -->
         <div class="right-button">
            <span class="yui-button yui-push-button" id="${el}-savesearch-button">
               <span class="first-child"><button>${msg("button.savesearch")}</button></span>
            </span>
         </div>
         
         <!-- Saved Searches menu button -->
         <div class="right-button">
            <span class="yui-button yui-push-button" id="${el}-savedsearches-button">
               <span class="first-child"><button>${msg("button.savedsearches")}</button></span>
            </span>
         </div>
      </div>
   </div>
   
   <div id="${el}-tabs" class="yui-navset">
      <ul class="yui-nav" id="${el}-tabset">
         <li class="selected"><a href="#${el}-critera-tab"><em>${msg("label.criteria")}</em></a></li>
         <li><a href="#${el}-results-tab"><em>${msg("label.results")}</em></a></li>
      </ul>            
      <div class="yui-content tab-content">
         <div id="${el}-critera-tab" class="terms">
            <span class="header">${msg("label.searchterm")}</span>
            <div>
               <span class="insertLabel">${msg("label.insertfield")}:</span>
               <span>
                  <input id="${el}-insertfield" type="button" name="insertfield" value="${msg("label.select")}" />
                  <select id="${el}-insertfield-menu">
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
                     <!-- double ?html encoding required here due to YUI bug -->
                     <#list meta as d>
                     <option value="${d.name}">${d.title?html?html}</option>
                     </#list>
                  </select>
               </span>
               <span class="insertDate">${msg("label.insertdate")}:</span>
               <div id="${el}-date" class="datepicker"></div>
               <a id="${el}-date-icon"><img src="${url.context}/components/form/images/calendar.png" class="datepicker-icon"/></a>
            </div>
            <div class="query">
               <!-- Query terms text input -->
               <textarea id="${el}-terms" rows="2" cols="40"></textarea>
            </div>
            <#include "../rmresults-common/rmoptions.ftl" />
            <div class="execute-search">
               <div class="search-button">
                  <span class="yui-button yui-push-button" id="${el}-search-button">
                     <span class="first-child"><button>${msg("button.search")}</button></span>
                  </span>
               </div>
            </div>
         </div>
         
         <div id="${el}-results-tab">
            <div class="yui-g">
               <div class="yui-u first">
                  <span id="${el}-itemcount"></span>
               </div>
               <div class="yui-u alignright">
                  <span class="print-button">
                     <span class="yui-button yui-push-button" id="${el}-print-button">
                        <span class="first-child"><button>${msg("button.print")}</button></span>
                     </span>
                  </span>
                  <span class="export-button">
                     <span class="yui-button yui-push-button" id="${el}-export-button">
                        <span class="first-child"><button>${msg("button.export")}</button></span>
                     </span>
                  </span>
               </div>
            </div>
            <!-- results inserted here into YUI Datagrid -->
            <div id="${el}-results" class="results"></div>
         </div>
      </div>
   </div>
</div>