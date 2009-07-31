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
      <div class="title">${msg("label.title")}</div>
      
      <div class="view-main">
         <div class="yui-gf">
            <div class="yui-u first">
               <div class="list-header">${msg("label.list-title")}</div>
               <div class="object-list">
                  <ul id="${el}-object-list">
                     <li id="${el}-recordSeries" class="recordSeries" title="${msg("label.recordseries")}">${msg("label.recordseries")}</li>
                     <li id="${el}-recordCategory" class="recordCategory" title="${msg("label.recordcategory")}">${msg("label.recordcategory")}</li>
                     <li id="${el}-recordFolder" class="recordFolder" title="${msg("label.recordfolder")}">${msg("label.recordfolder")}</li>
                     <li id="${el}-record" class="record" title="${msg("label.record")}">${msg("label.record")}</li>
                  </ul>
               </div>
            </div>
            <div class="yui-u separator">
               <div class="right">
                  <!-- New Metadata Property button -->
                  <div class="newproperty-button">
                     <span class="yui-button yui-push-button" id="${el}-newproperty-button">
                        <span class="first-child"><button>${msg("button.new")}</button></span>
                     </span>
                  </div>
               </div>
               <div class="list-title">
                  <span>${msg("label.custom-metadata")}:&nbsp;</span>
                  <span id="${el}-metadata-item"></span>
               </div>
               <!-- dynamically generated property list -->
               <div id="${el}-property-list"></div>
            </div>
         </div>
      </div>
   </div>

</div>