<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiToolbar("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   ).setTitle("${page.url.args["title"]!""}");
//]]></script>
<div id="${args.htmlid}-body" class="toolbar">
   <div class="header">
        <div class="new-page"><button id="${args.htmlid}-create-button">${msg("button.create")}</button></div>
        <div class="separator">|</div>
        <div class="delete-page"><button id="${args.htmlid}-delete-button">${msg("button.delete")}</button></div>
   </div>
   <div id="${args.htmlid}-createpanel">
      <div class="hd">${msg("panel.create.title")}</div>
      <div class="bd">
         <input type="text" id="${args.htmlid}-title" name="title" value="" size="30"/>
         <button id="${args.htmlid}-save-button">${msg("button.save")}</button>
      </div>
      <div class="ft">${msg("panel.create.footer")}</div>
   </div>
</div>