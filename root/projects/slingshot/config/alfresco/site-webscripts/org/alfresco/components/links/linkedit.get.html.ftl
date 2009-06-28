<#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
<script type="text/javascript">//<![CDATA[
   new Alfresco.LinkEdit("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "links",
      <#if page.url.args.linkId??>
         editMode: true,
         linkId: "${page.url.args.linkId?html}"
      <#else>
         editMode: false,
         linkId: ""
      </#if>
   }).setMessages(
      ${messages}
   );
//]]></script>

<#if page.url.args.linkId??>
   <h1>${msg("form.editLink")}</h1>
<#else>
   <h1>${msg("form.createLink")}</h1>
</#if>

<div id="${args.htmlid}-div" class="editLinkForm hidden">
   <form id="${args.htmlid}-form" method="post" action="">
      <div>          
         <div>
            <label for="${args.htmlid}-title">${msg("form.title")}:</label>
            <input class="lbl" type="text" id="${args.htmlid}-title" name="title" value="" tabindex="1"/>
            <span class="lbl dot">*</span>
         </div>
         <div>
            <label for="${args.htmlid}-description">${msg("form.description")}:</label>
            <textarea class="lbl" id="${args.htmlid}-description" type="textarea" rows="5" name="description" tabindex="2"></textarea>
         </div>
         <div>
            <label for="${args.htmlid}-url">${msg("form.url")}:</label>
	        <input class="lbl" id="${args.htmlid}-url" type="text" name="url" tabindex="3"/>
            <span class="lbl dot">*</span>
         </div>
         <div>
            <label for="${args.htmlid}-internal">${msg("form.internal")}:</label>
            <input class="internal" id="${args.htmlid}-internal" type="checkbox" name="internal" tabindex="4"/>
            <span class="lbl help">${msg("form.internalDescription")}</span>
         </div>
         <div>
            <label for="${args.htmlid}-tag-input-field">${msg("form.tags")}:</label>
            <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
         </div>
         <input type="hidden" name="page" value="links-view"/>
      </div>
      <div class="nodeFormAction">
         <input type="submit" id="${args.htmlid}-ok" value="" tabindex="7"/>
	     <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="8"/>
      </div>
   </form>
</div>