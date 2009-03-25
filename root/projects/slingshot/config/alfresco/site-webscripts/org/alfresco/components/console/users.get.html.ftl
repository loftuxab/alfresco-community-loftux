<script type="text/javascript">//<![CDATA[
   new Alfresco.ConsoleUsers("${args.htmlid}").setOptions(
   {
      minSearchTermLength: "${args.minSearchTermLength!'3'}",
      maxSearchResults: "${args.maxSearchResults!'100'}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="users list">
   
   <div class="yui-t1">
      <div class="yui-b">
         <div class="yui-g">
            <div class="yui-u first">
               <div class="title"><label for="${args.htmlid}-search-text">${msg("label.title")}</label></div>
            </div>
            <div class="yui-u align-right">
               <div class="newuser-button"><button id="${args.htmlid}-newuser-button">${msg("button.newuser")}</button></div>
            </div>
         </div>
         <div class="yui-g">
            <div class="yui-u first">
               <div class="search-text"><input type="text" id="${args.htmlid}-search-text" name="-" value="" />
                  <div class="search-button"><button id="${args.htmlid}-search-button">${msg("button.search")}</button></div>
               </div>
            </div>
            <div class="yui-u align-right">
               Sort by: DROPDOWN
            </div>
         </div>
         <div class="search-bar theme-bg-color-3">
            Over 100 search results. Show first 100 only.
         </div>
         <div id="${args.htmlid}-results" class="results">
            DATATABLE
         </div>
      </div>
   </div>
</div>