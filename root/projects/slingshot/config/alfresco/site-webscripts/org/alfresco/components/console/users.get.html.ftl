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
   
   <div class="yui-g">
      <div class="yui-u first">
         <div class="title"><label for="${args.htmlid}-search-text">${msg("label.title")}</label></div>
      </div>
      <div class="yui-u align-right">
         <!-- New User button -->
         <div class="newuser-button">
            <span class="yui-button yui-push-button" id="${args.htmlid}-newuser-button">
               <span class="first-child"><button>${msg("button.newuser")}</button></span>
            </span>
         </div>
      </div>
   </div>
   <div class="yui-g separator">
      <div class="yui-u first">
         <div class="search-text"><input type="text" id="${args.htmlid}-search-text" name="-" value="" />
            <!-- Search button -->
            <div class="search-button">
               <span class="yui-button yui-push-button" id="${args.htmlid}-search-button">
                  <span class="first-child"><button>${msg("button.search")}</button></span>
               </span>
            </div>
         </div>
      </div>
      <div class="yui-u align-right">
         View: DROPDOWN
      </div>
   </div>
   <div class="panel">
      <div id="${args.htmlid}-search-bar" class="search-bar theme-bg-color-3">${msg("message.noresults")}</div>
      <div class="results" id="${args.htmlid}-datatable"></div>
   </div>
   
</div>