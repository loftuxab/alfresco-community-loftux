<script type="text/javascript">//<![CDATA[
   new Alfresco.module.DoclibDetails("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-dialog" class="details">
   <div id="${args.htmlid}-title" class="hd"></div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="post">
         <div class="yui-g">
            <h2>${msg("header.metadata")}:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="properties.name" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-title">${msg("label.title")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="properties.title" /></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="properties.description" rows="3" cols="20"></textarea></div>
         </div>
         <div class="yui-g">
            <h2>${msg("header.tags")}:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-tags">${msg("label.tags")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-tags" type="text" name="tags" /><br />${msg("label.tags.hint")}</div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>
