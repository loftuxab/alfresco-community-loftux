<!--[if IE]>
<iframe id="yui-history-iframe" src="${url.context}/yui/assets/blank.html"></iframe> 
<![endif]-->
<input id="yui-history-field" type="hidden" />

<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsMetaData("${args.htmlid}").setOptions(
   {
   }).setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="metadata">

   <!-- View panel -->
   <div id="${el}-view" class="hidden">
      <div class="title">${msg("label.custom-metadata-title")}</div>
      
      <div class="view-main">
         <div class="yui-gf">
            <div class="yui-u first">
               <div class="list-header theme-bg-color-4">${msg("label.list-title")}</div>
               <div class="object-list">
                  <ul id="${el}-object-list">
                     <li id="${el}-recordSeries" class="recordSeries theme-bg-color-2" title="${msg("label.recordseries")}">${msg("label.recordseries")}</li>
                     <li id="${el}-recordCategory" class="recordCategory theme-bg-color-2" title="${msg("label.recordcategory")}">${msg("label.recordcategory")}</li>
                     <li id="${el}-recordFolder" class="recordFolder theme-bg-color-2" title="${msg("label.recordfolder")}">${msg("label.recordfolder")}</li>
                     <li id="${el}-record" class="record theme-bg-color-2" title="${msg("label.record")}">${msg("label.record")}</li>
                  </ul>
               </div>
            </div>
            <div class="yui-u separator">
               <div class="list-title-button">
                  <!-- New Metadata Property button -->
                  <div class="newproperty-button">
                     <span class="yui-button yui-push-button" id="${el}-newproperty-button">
                        <span class="first-child"><button>${msg("button.new")}</button></span>
                     </span>
                  </div>
               </div>
               <div class="list-title theme-bg-color-4">
                  <span>${msg("label.custom-metadata")}:&nbsp;</span>
                  <span id="${el}-view-metadata-item"></span>
               </div>
               <!-- dynamically generated property list -->
               <div id="${el}-property-list"></div>
            </div>
         </div>
      </div>
   </div>
   
   <!-- Create panel -->
   <div id="${el}-create" class="hidden">
      <div class="title">
         <span>${msg("label.create-metadata-title")}:&nbsp;</span>
         <span id="${el}-create-metadata-item"></span>
      </div>
      
      <form id="${el}-create-form">
      <div class="create-main">
         <div class="label-row">
            <span>${msg("label.label")}:</span>
         </div>
         <div class="field-row">
            <input id="${el}-create-label" type="text" maxlength="255" />&nbsp;*
         </div>
         <div class="label-row">
            <span>${msg("label.type")}:</span>
         </div>
         <div class="field-row">
            <select id="${el}-create-type">
               <option value="d:text">${msg("label.datatype.text")}</option>
               <option value="d:boolean">${msg("label.datatype.boolean")}</option>
               <option value="d:date">${msg("label.datatype.date")}</option>
            </select>
         </div>
         <div class="field-row">
            <input type="checkbox" id="${el}-create-use-list" /><label for="${el}-create-use-list">${msg("label.use-list")}:</label>
            <!-- TODO: generate list of values drop-down-->
            <select id="${el}-create-list">
               <option value="">REGIONS</option>
            </select>
         </div>
         <div class="field-row">
            <input type="checkbox" id="${el}-create-mandatory" /><label for="${el}-create-mandatory">${msg("label.mandatory")}</label>
         </div>
         <div class="button-row">
            <!-- Create Metadata Property button -->
            <span class="yui-button yui-push-button" id="${el}-createproperty-button">
               <span class="first-child"><button>${msg("button.create")}</button></span>
            </span>
            <!-- Cancel Metadata Property button -->
            <span class="yui-button yui-push-button" id="${el}-cancelcreateproperty-button">
               <span class="first-child"><button>${msg("button.cancel")}</button></span>
            </span>
         </div>
      </div>
      </form>
   </div>

</div>