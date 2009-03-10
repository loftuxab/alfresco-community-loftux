<div id="${args.htmlid}-configDialog">
   <div class="hd">${msg("label.title")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-webviewTitle">${msg("label.linkTitle")}</label></div>
            <div class="yui-u"><input id="${args.htmlid}-webviewTitle" type="text" name="webviewTitle" tabindex="1" value="${(webviewTitle!"")?html}"/>&nbsp;</div>
            <div class="yui-u first"><label for="${args.htmlid}-url">${msg("label.url")}</label></div>
            <div class="yui-u"><input id="${args.htmlid}-url" type="text" name="url" tabindex="2" value="${(uri!"")?html}"/>&nbsp;*</div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="3" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="4" />
         </div>
      </form>
   </div>
</div>