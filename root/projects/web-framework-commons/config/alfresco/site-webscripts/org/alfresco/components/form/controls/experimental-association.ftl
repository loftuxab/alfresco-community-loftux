<#if form.mode == "view">
<div class="viewmode-field">
   <span class="viewmode-label">${field.label?html}:</span>
   <span class="viewmode-value">${field.value?html}</span>
</div>
<#else>
<label for="${args.htmlid}-${field.id}">${field.label?html}:<#if field.endpointMandatory><span class="mandatory-indicator">*</span></#if></label>

<script type="text/javascript">//<![CDATA[
   new Alfresco.ObjectFinder("${args.htmlid}-${field.id}").setOptions(
   {
      currentValue: "${field.value}",
      itemType: "${field.endpointType}",
      minSearchTermLength: "${args.minSearchTermLength!'3'}",
      maxSearchResults: "${args.maxSearchResults!'100'}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-${field.id}" class="object-finder list compact">
   
   <div>
      <span id="${args.htmlid}-${field.id}-current-value">${field.value}</span>&nbsp;
      <button id="${args.htmlid}-${field.id}-select-button">${msg("form.control.association.button.select")}</button>
   </div>
   
   <div id="${args.htmlid}-${field.id}-picker" class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${args.htmlid}-${field.id}-search-text" value="" /></div>
         <div class="search-button"><button id="${args.htmlid}-${field.id}-search-button">${msg("button.search")}</button></div>
      </div>
      
      <div id="${args.htmlid}-${field.id}-results" class="results"></div>
   </div>

   <input type="hidden" id="${args.htmlid}-${field.id}-added" name="${field.name}_added" />
   <input type="hidden" id="${args.htmlid}-${field.id}-removed" name="${field.name}_removed" />
   
</div>
       
</#if>