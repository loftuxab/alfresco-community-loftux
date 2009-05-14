<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsSearch("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="search">
   <div class="title">${msg("label.searchtitle")}</div>
   
   <div class="builder">
      <div class="yui-g">
         <div class="yui-u first">
            ${msg("label.metadata")}:
         </div>
         <div class="yui-u">
            ${msg("label.order")}:
         </div>
      </div>
      <div>${msg("label.searchterm")}:</div>
      <div>
         <!-- Query text input -->
         <textarea id="${args.htmlid}-query" rows="2" cols="120"></textarea>
         <!-- Search button -->
         <div class="search-button">
            <span class="yui-button yui-push-button" id="${args.htmlid}-search-button">
               <span class="first-child"><button>${msg("button.search")}</button></span>
            </span>
         </div>
      </div>
   </div>
   
   <div id="${args.htmlid}-results" class="results"></div>
</div>