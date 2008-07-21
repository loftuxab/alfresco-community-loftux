<div id="${args.htmlid}-dialog" class="move-to">
   <div class="hd">${msg("title")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="post">
         <div class="yui-g">
            <h2>${msg("header")}:</h2>
         </div>
         <div id="${args.htmlid}-treeview" class="treeview"></div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.move")}" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.DocListMoveTo");
//]]></script>
