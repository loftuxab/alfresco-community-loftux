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
   <h1>${msg("editLink")}</h1>
<#else>
   <h1>${msg("createLink")}</h1>
</#if>

<div id="${args.htmlid}-div" class="editLinkForm hidden">
   <form id="${args.htmlid}-form" method="post" action="">
      <div>          
         <!-- title -->
         <div>
            <label>${msg("title")}:</label>
            <input type="text" id="${args.htmlid}-title" name="title" value="" /> *
         </div>

         <!-- content -->
         <div>
            <label>${msg("description")}:</label>
            <textarea id="${args.htmlid}-description" type="textarea" rows="3" name="description" tabindex="2"></textarea>
         </div>

         <!-- url -->
         <div>
            <label>${msg("url")}:</label>
	        <input id="${args.htmlid}-url" type="text" name="url" tabindex="3"/> *
         </div>

         <!-- internal -->
         <div>
            <label>${msg("internal")}:</label>
            <input id="${args.htmlid}-internal" class="internal" type="checkbox" name="isinternal"/>
         </div>

         <!-- tags -->
         <label>${msg("tags")}:</label>
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
         <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
         <!-- end tags -->

         <input type="hidden" name="page" value="links-view"/>
      </div>
      <div class="nodeFormAction">
         <input type="submit" id="${args.htmlid}-ok" value="" tabindex="6"/>
	     <input type="button" id="${args.htmlid}-cancel" value="${msg("button.cancel")}" tabindex="7"/>
      </div>
   </form>
</div>