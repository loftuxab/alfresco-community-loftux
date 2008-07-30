<div id="${args.htmlid}-dialog" class="create-folder">
   <div class="hd">${msg("title")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="post">
         <div class="yui-g">
            <h2>${msg("header")}:</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="name" tabindex="1" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-title">${msg("label.title")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="title" tabindex="2" /></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="description" rows="3" cols="20" tabindex="3" ></textarea></div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="4" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="5" />
         </div>
      </form>
   </div>
</div>
