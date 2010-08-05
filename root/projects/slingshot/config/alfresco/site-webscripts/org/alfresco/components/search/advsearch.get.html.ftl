<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.AdvancedSearch("${el}").setOptions(
   {
      siteId: "${siteId}",
      searchForms: [<#list searchForms as f>
      {
         id: "${f.id}",
         type: "${f.type}",
         label: "${f.label?js_string}",
         description: "${f.description?js_string}"
      }<#if f_has_next>,</#if></#list>]
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="search">
   
   <div class="yui-gc form-row">
      <div class="yui-u first">
         <span class="lookfor">${msg("label.lookfor")}:</span>
         
         <div style="position:relative">
            <div class="selected-form theme-bg-2 theme-border-3">
               <ul>
                  <li>
                     <a id="${el}-selected-form-link" href="#">
                        <div id="${el}-selected-form-type" class="form-type-name"></div>
                        <div id="${el}-selected-form-desc" class="form-type-description"></div>
                     </a>
                  </li>
               </ul>
            </div>
            
            <div class="form-list theme-bg-2 theme-border-3 hidden">
               <ul id="${el}-form-list"></ul>
            </div>
         </div>
      </div>
      
      <div class="yui-u align-right">
         <span id="${el}-search-button" class="yui-button yui-push-button search-icon">
            <span class="first-child">
               <button type="button">${msg('button.search')}</button>
            </span>
         </span>
      </div>
   </div>
   
   <div class="keywords-box">
      <div>${msg("label.keywords")}:</div>
      <input type="text" class="terms" name="${el}-search-text" id="${el}-search-text" value="" maxlength="1024" />
   </div>
   
   <div class="share-form form-container">
      <div id="${el}-form" class="form-fields"></div>
   </div>
   
</div>