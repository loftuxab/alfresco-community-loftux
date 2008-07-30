<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiToolbar("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   ).setTitle("${page.url.args["title"]!""}").setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar">
   <div class="header">
        <div class="new-page"><button id="${args.htmlid}-create-button">${msg("button.create")}</button></div>
        <div class="separator">|</div>
        <div class="delete-page"><button id="${args.htmlid}-delete-button">${msg("button.delete")}</button></div>
        <div class="separator">|</div>
        <div class="rename-page"><button id="${args.htmlid}-rename-button">${msg("button.rename")}</button></div>
   </div>
   <div id="${args.htmlid}-createpanel">
      <div class="hd">${msg("panel.create.title")}</div>
      <div class="bd">
         <form id="${args.htmlid}-addPageForm" method="get" action="${url.context}/page/site/${page.url.templateArgs["site"]}/wiki-page">
            <fieldset>
               <input type="hidden" id="${args.htmlid}-title" name="title"/>
               <input type="text" id="${args.htmlid}-pagetitle" name="pagetitle" value="" size="30"/>
               <input type="submit" id="${args.htmlid}-save-button" value="${msg("button.save")}"/>
            </fieldset>
         </form>
      </div>
      <div class="ft">${msg("panel.create.footer")}</div>
   </div>
   <div id="${args.htmlid}-renamepanel">
        <div class="hd">${msg("panel.rename.title")}</div>
        <div class="bd">
           <input type="text" id="${args.htmlid}-newname" name="name" value="" size="30"/>
           <button id="${args.htmlid}-rename-save-button">${msg("button.save")}</button>
        </div>
        <div class="ft">${msg("panel.rename.footer")}</div>
     </div>   
</div>