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
      <div class="leftcolumn" style="margin-top: 10px; margin-bottom: 10px;">
            <span class="label">${msg("label.title")}:</span>
            <span class="input"><input type="text" maxlength="256" size="75" id="${args.htmlid}-pageTitle" name="pageTitle"/></span>
      </div>
      
         <span class="label">${msg("label.text")}:</span>
         <textarea class="yuieditor" name="pagecontent" id="${args.htmlid}-pagecontent" cols="180" rows="10"></textarea>
      
      <!-- tags -->
         <span class="label">${msg("label.tags")}:</span>
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>

         <!-- Render the tag inputs -->
         <@taglibraryLib.renderTagInputs htmlid=args.htmlid tagInputName="tags" tags=[] />
         <!-- Render the library component -->
         <@taglibraryLib.renderTagLibrary htmlid=args.htmlid site=page.url.templateArgs.site tags=[] />
         <!-- end tags -->
      
      <div class="formAction">
         <input type="submit" id="${args.htmlid}-save-button" value="${msg("button.save")}" />
         <a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki" id="${args.htmlid}-cancel-button">${msg("button.cancel")}</a>
      </div>
      </form>
</div>