<div id="${args.htmlid}-dialog" class="site-folder">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <div class="yui-g">
         <h2 id="${args.htmlid}-header">${msg("header")}</h2>
      </div>
      <div id="${args.htmlid}-treeview" class="treeview"></div>
      <div class="bdft">
         <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" />
         <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
      </div>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.addMessages(${messages}, "Alfresco.module.RecordsCopyMoveFileTo");
//]]></script>