<div id="${args.htmlid}-configDialog" class="config-feed">
   <div class="hd">Enter URL:</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="POST">
         <div class="yui-g">
            <h2>${msg("header")}:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-url">${msg("label.url")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-url" type="text" name="url" value=""/>&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label>${msg("label.displayItems")}:</label></div>
            <div class="yui-u">
               <select id="${args.htmlid}-limit" name="limit">
                <option value="all">All</option>
                <#assign limits = [5, 10, 15, 20, 25]>
                <#list limits as limit><option value="${limit}">${limit}</option></#list>
               </select>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-new_window">${msg("label.newWindow")}:</label></div>
            <div class="yui-u"><input type="checkbox" id="${args.htmlid}-new_window" name="new_window" /></div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>
