<div id="${args.htmlid}-dialog" class="create-folder">
   <div id="${args.htmlid}-dialogTitle" class="hd">${msg("title")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="post">
         <div class="yui-g">
            <h2 id="${args.htmlid}-dialogHeader">${msg("header")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-name">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-name" type="text" name="name" tabindex="0" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-title">${msg("label.title")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="title" tabindex="0" /></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="description" rows="3" cols="20" tabindex="0" ></textarea></div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
