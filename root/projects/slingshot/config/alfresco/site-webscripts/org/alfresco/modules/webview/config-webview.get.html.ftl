<div id="${args.htmlid}-configDialog">
   <div class="hd">${msg("label.title")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-webviewTitle">${msg("label.linkTitle")}</label></div>
            <div class="yui-u"><input id="${args.htmlid}-webviewTitle" type="text" name="webviewTitle" tabindex="0" value="${(webviewTitle!"")?html}" maxlength="256"/>&nbsp;</div>
            <div class="yui-u first"><label for="${args.htmlid}-url">${msg("label.url")}</label></div>
            <div class="yui-u"><input id="${args.htmlid}-url" type="text" name="url" tabindex="0" value="${(uri!"")?html}"/>&nbsp;*</div>
            <div class="yui-u first"><label for="${args.htmlid}-height">${msg("label.height")}</label></div>
            <div class="yui-u"><input id="${args.htmlid}-height" type="text" name="height" tabindex="0" value="${(height!"600")?html}" style="width: 3em" /> ${msg("label.height.units")}</div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>