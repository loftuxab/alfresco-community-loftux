<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiToolbar("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   ).setTitle("${page.url.args["title"]!""}").setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar flat-button">

   <div class="navigation-bar">
      <div>
         <#if args.showBackLink == "true">
         <span class="forwardLink">
            <a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki">${msg("link.listView")}</a>
         </span>
         </#if>
         <span class="forwardLink">
            <a href="${url.context}/page/site/${page.url.templateArgs.site!""}/wiki-page?filter=main&amp;title=Main_Page">${msg("link.mainPage")}</a>
         </span>
      </div>
   </div>

   <div class="action-bar">
      <div class="new-page"><a href="${page.url.context}/page/site/${page.url.templateArgs["site"]}/wiki-create" id="${args.htmlid}-create-button">${msg("button.create")}</a></div>
      <div class="separator">&nbsp;</div>
      <div class="delete-page"><a href="#" id="${args.htmlid}-delete-button">${msg("button.delete")}</a></div>
      <div class="separator">&nbsp;</div>
      <div class="rename-page"><a href="#" id="${args.htmlid}-rename-button">${msg("button.rename")}</a></div>
   </div>

   <div class="rss-feed">
      <div>
         <a id="${args.htmlid}-rssFeed-button" href="${url.context}/proxy/alfresco-rss/slingshot/wiki/pages/${page.url.templateArgs["site"]}?format=rss">${msg("message.rssfeed")}</a>
      </div>
   </div>

   <div id="${args.htmlid}-renamepanel" class="rename-panel">
      <div class="hd"><label for="${args.htmlid}-renameTo">${msg("panel.rename.title")}</label></div>
      <div class="bd">
         <form id="${args.htmlid}-renamePageForm" method="post" action="${url.context}/proxy/alfresco/slingshot/wiki/page/${page.url.templateArgs["site"]}/${page.url.args["title"]!""}">
            <input type="hidden" id="${args.htmlid}-page" name="page" value="wiki-page" />
            <div class="yui-ge">
               <div class="yui-u first">
                  <input type="text" id="${args.htmlid}-renameTo" name="name" value="" size="30" tabindex="1" />
               </div>
               <div class="yui-u">
                  <input type="submit" id="${args.htmlid}-rename-save-button" value="${msg("button.save")}" tabindex="2" />
               </div>
            </div>
         </form>
         <div class="bdft">${msg("panel.rename.footer")}</div>
      </div>
   </div>   
</div>
<div class="clear"></div>