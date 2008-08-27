<div id="${args.htmlid}-dialog" class="create-site">
   <div class="hd">${msg("header.createSite")}</div>
   <div class="bd">
      <form id="${args.htmlid}-form" action="${url.serviceContext}/modules/create-site" method="POST">
         <input type="hidden" id="${args.htmlid}-isPublic" name="isPublic" value="true"/>
         <div class="yui-g">
            <h2>${msg("section.info")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">${msg("label.name")}</div>
            <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="title" tabindex="1"/>&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${args.htmlid}-shortName">${msg("label.shortName")}</label></div>
            <div class="yui-u"><input id="${args.htmlid}-shortName" type="text" name="shortName" tabindex="2"/>&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">${msg("label.description")}</div>
            <div class="yui-u"><textarea id="${args.htmlid}-description" name="description" rows="3" tabindex="3"></textarea></div>
         </div>
         <div class="yui-g">
            <h2>${msg("section.type")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">${msg("label.type")}</div>
            <div class="yui-u">
               <select id="${args.htmlid}-sitePreset" name="sitePreset" tabindex="4">
                  <#list sitePresets as sitePreset>
                     <option value="${sitePreset.id}">${sitePreset.name}</option>
                  </#list>
               </select>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first">${msg("label.isPublic")}</div>
            <div class="yui-u"><input id="${args.htmlid}-isPublic-checkbox" name="isPublic" type="checkbox" checked="checked" tabindex="5"/> ${msg("text.isPublic")}</div>
         </div>
         <div class="bdft">
            <input type="submit" id="${args.htmlid}-ok-button" value="${msg("button.ok")}" tabindex="6"/>
            <input type="button" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" tabindex="7"/>
         </div>
      </form>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.CreateSite");
//]]></script>
