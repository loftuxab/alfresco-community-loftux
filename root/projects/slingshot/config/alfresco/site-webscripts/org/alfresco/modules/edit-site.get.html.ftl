<div id="${args.htmlid}-dialog" class="edit-site">
   <div class="hd">${msg("header.editSite")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" method="PUT"  action="">
         <input type="hidden" id="${args.htmlid}-visibility" name="visibility" value="${profile.visibility}"/>
         <input id="${args.htmlid}-shortName" type="hidden" name="shortName" value="${profile.shortName}"/>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-title">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="title" value="${profile.title?html}" tabindex="0"/>&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="description" rows="3" tabindex="0">${profile.description?html}</textarea></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-isPublic">${msg("label.access")}:</label></div>
            <div class="yui-u">
               <input id="${args.htmlid}-isPublic" type="radio" <#if (profile.visibility == "PUBLIC" || profile.visibility == "MODERATED")>checked="checked"</#if> tabindex="0" name="-" /> ${msg("label.isPublic")}<br />
               <div class="moderated">
                  <input id="${args.htmlid}-isModerated" type="checkbox" tabindex="0" name="-" <#if (profile.visibility == "MODERATED")>checked="checked"</#if> <#if (profile.visibility == "PRIVATE")>disabled="true"</#if>/> ${msg("label.isModerated")}<br />
                  <span class="help"><label for="${args.htmlid}-isModerated">${msg("label.moderatedHelp")}</label></span>
               </div>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">&nbsp;</div>
            <div class="yui-u">
               <input id="${args.htmlid}-isPrivate" type="radio" tabindex="0" name="-" <#if (profile.visibility == "PRIVATE")>checked="checked"</#if>/> ${msg("label.isPrivate")}
            </div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok-button" value="${msg("button.ok")}" tabindex="0"/>
            <input type="button" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" tabindex="0"/>
         </div>
      </form>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.EditSite");
//]]></script>