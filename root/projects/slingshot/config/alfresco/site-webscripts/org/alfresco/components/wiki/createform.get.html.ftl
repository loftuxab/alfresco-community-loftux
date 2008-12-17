<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiCreateForm("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   ).setMessages(
      ${messages}
   );
//]]></script>
<div class="wikipage-header">
	<div class="back-nav">
		<span class="backLink">
			<a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki">
				${msg("header.back")}
			</a>
		</span>
	</div>
</div>
<div id="${args.htmlid}-pagecreate" class="wikipage">
      <h1>${msg("header.create")}</h1>
      <#-- The "action" attribute is set dynamically upon form submission -->
      <form id="${args.htmlid}-form" action="" method="post">
         <input type="hidden" id="${args.htmlid}-page" name="page" value="wiki-page" />
         <div class="leftcolumn" style="margin-top: 10px; margin-bottom: 10px;">
            <span class="label"><label for="${args.htmlid}-pageTitle">${msg("label.title")}:</label></span>
            <span class="input"><input type="text" maxlength="256" size="75" id="${args.htmlid}-pageTitle" name="pageTitle"/></span>
         </div>
      
         <span class="label" for="${args.htmlid}-pagecontent">${msg("label.text")}:</span>
         <textarea class="yuieditor" name="pagecontent" id="${args.htmlid}-pagecontent" cols="180" rows="10"></textarea>
      
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