<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiCreateForm("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
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
<div id="${args.htmlid}-pagecreate">
      <#-- "action" is set dynamically upon form submission -->
      <form id="${args.htmlid}-form" action="" method="post">
      <div class="leftcolumn" style="margin-top: 10px; margin-bottom: 10px;">
            <span class="label">${msg("label.title")}:</span>
            <span class="input"><input type="text" maxlength="256" size="50" id="${args.htmlid}-pageTitle" name="pageTitle"/></span>
      </div>
      <textarea name="pagecontent" id="${args.htmlid}-pagecontent" cols="50" rows="10"></textarea>
      <!-- tags -->
   
      <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
      
      <!-- Render the tag inputs -->
      <@taglibraryLib.renderTagInputs htmlid=args.htmlid tagInputName="tags" tags=[] />
      <!-- Render the library component -->
      <@taglibraryLib.renderTagLibrary htmlid=args.htmlid site=page.url.templateArgs.site tags=[] />
      <!-- end tags -->
      <div>
         <input type="submit" id="${args.htmlid}-save-button" value="${msg("button.save")}" />
	      <input type="submit" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" />
      </div>
      </form>
</div>