<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiToolbar("${args.htmlid}").setSiteId(
      "${page.url.args["site"]!""}"
   );
//]]></script>
<div id="${args.htmlid}-body" class="toolbar">
   <div>
        <div class="new-page"><button id="${args.htmlid}-create-button" class="create-page">${msg("button.create")}</button></div>
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