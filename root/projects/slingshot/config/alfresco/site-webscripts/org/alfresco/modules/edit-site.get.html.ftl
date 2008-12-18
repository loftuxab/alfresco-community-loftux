<div id="${args.htmlid}-dialog" class="edit-site">
   <div class="hd">${msg("header.editSite")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" method="PUT"  action="">
         <input type="hidden" id="${args.htmlid}-isPublic" name="isPublic" value="${profile.isPublic?string}"/>
         <input id="${args.htmlid}-shortName" type="hidden" name="shortName" value="${profile.shortName}"/>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-title">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="title" value="${profile.title?html}" tabindex="1"/>&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="description" rows="3" tabindex="3">${profile.description?html}</textarea></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-isPublic-checkbox">${msg("label.isPublic")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-isPublic-checkbox" type="checkbox" <#if (profile.isPublic)>checked="checked" </#if>tabindex="5"/> ${msg("text.isPublic")}</div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok-button" value="${msg("button.ok")}" tabindex="6"/>
            <input type="button" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" tabindex="7"/>
         </div>
      </form>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.EditSite");
//]]></script>