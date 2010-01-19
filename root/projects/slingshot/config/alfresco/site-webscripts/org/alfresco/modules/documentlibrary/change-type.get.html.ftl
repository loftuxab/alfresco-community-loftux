<div id="${args.htmlid}-dialog" class="change-type">
   <div id="${args.htmlid}-dialogTitle" class="hd">${msg("title")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="" method="post">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-type">${msg("label.type")}:</label></div>
            <div class="yui-u">
               <select id="${args.htmlid}-type" type="text" name="type" tabindex="1">
                  <option value="-">${msg("label.select")}</option>
               <#list types.selectable as t>
                  <option value="${t}">${msg(t?replace(":", "_"))}</option>
               </#list>
               </select>&nbsp;*
            </div>
         </div>
         <div class="bdft">
            <input type="button" id="${args.htmlid}-ok" value="${msg("button.ok")}" tabindex="4" />
            <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="5" />
         </div>
      </form>
   </div>
</div>
