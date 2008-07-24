<div id="${args.htmlid}-dialog" class="move-to">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <div class="yui-g">
         <h2>${msg("header")}:</h2>
      </div>
      <div id="${args.htmlid}-treeview" class="treeview"></div>
      <div class="bdft">
         <input type="button" id="${args.htmlid}-ok" value="${msg("button.move")}" />
         <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
      </div>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.DoclibMoveTo");
//]]></script>
