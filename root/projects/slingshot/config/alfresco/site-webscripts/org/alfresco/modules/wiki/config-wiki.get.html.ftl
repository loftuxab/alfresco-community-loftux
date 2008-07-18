<div id="${args.htmlid}-configDialog" class="config-feed">
   <div class="hd">Select wiki page</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="POST">
         <input type="hidden" name="siteId" value="${url.templateArgs.siteId}"/>
         <div class="yui-g">
            <h2>${msg("header")}:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-url">${msg("label.url")}:</label></div>
            <div class="yui-u">
            <#if pageList.pages?size &gt; 0>
            <select name="wikipage">
            <#list pageList.pages as p>
               <option value="${p.name}">${p.title}</option>
            </#list>
            </select>
            </#if>
            </div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>