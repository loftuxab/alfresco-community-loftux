<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiCreateForm("${args.htmlid}").setOptions(
      {
         locale:'${locale?substring(0, 2)}'
      }).setSiteId(
      "${page.url.templateArgs["site"]!""}"
   ).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-pagecreate" class="wikipage">
   <h1>${msg("header.create")}</h1>
   <hr/>
      <#-- The "action" attribute is set dynamically upon form submission -->
      <form id="${args.htmlid}-form" action="" method="post">
         <input type="hidden" id="${args.htmlid}-page" name="page" value="wiki-page" />
         <div class="leftcolumn">
            <span class="label"><label for="${args.htmlid}-title">${msg("label.title")}:</label></span>
            <span class="input"><input type="text" maxlength="256" size="75" id="${args.htmlid}-title" name="pageTitle"/></span>
         </div>
      
         <span class="label" for="${args.htmlid}-content">${msg("label.text")}:</span>
         <textarea class="yuieditor" name="pagecontent" id="${args.htmlid}-content" cols="180" rows="10"></textarea>
      
      <!-- tags -->
         <span class="label"><label for="${htmlid}-tag-input-field">${msg("label.tags")}:</label></span>
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
         
         <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
         <!-- end tags -->
      
      <div class="formAction">
         <input type="submit" id="${args.htmlid}-save-button" value="${msg("button.save")}" />
         <a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki" id="${args.htmlid}-cancel-button">${msg("button.cancel")}</a>
      </div>
      </form>
</div>
