<div id="${args.htmlid}-dialog" class="copy-to">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <div id="${args.htmlid}-wrapper" class="wrapper">
         <div class="mode">
            <h3>${msg("header.destination-type")}</h3>
            <div id="${args.htmlid}-modeGroup" class="yui-buttongroup">
               <input type="radio" id="${args.htmlid}-site" name="site" value="${msg("button.site")}" checked="checked" />
               <input type="radio" id="${args.htmlid}-repository" name="repository" value="${msg("button.repository")}" />
            </div>
         </div>
         <div class="site">
            <h3>${msg("header.site-picker")}</h3>
            <div id="${args.htmlid}-sitePicker" class="site-picker"></div>
         </div>
         <div class="container">
            <h3>${msg("header.container-picker")}</h3>
            <div id="${args.htmlid}-containerPicker" class="container-picker"></div>
         </div>
         <div class="path">
            <h3>${msg("header.path-picker")}</h3>
            <div id="${args.htmlid}-treeview" class="treeview"></div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.copy")}" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
         </div>
      </div>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.DoclibCopyTo");
//]]></script>
