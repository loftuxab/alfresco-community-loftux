<div id="${args.htmlid}-dialog" class="revert-wiki-version">
   <div class="hd">${msg("header.revert")}</div>
   <div class="bd">
         <div class="bdbd">
            <p id="${args.htmlid}-prompt-span"></p>
         </div>
         <div class="bdft">
            <input id="${args.htmlid}-ok-button" type="button" value="${msg("button.ok")}" />
            <input id="${args.htmlid}-cancel-button" type="button" value="${msg("button.cancel")}" />
         </div>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.RevertWikiVersion");
//]]></script>
