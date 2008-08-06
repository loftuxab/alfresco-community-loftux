<script type="text/javascript">//<![CDATA[
	new Alfresco.Wiki("${args.htmlid}").setOptions({
	   "siteId": "${page.url.templateArgs.site}",
	   "pageTitle": "${page.url.args["title"]!""}",
	   "mode": "${page.url.args["action"]!"view"}"
	});
//]]></script>
<div class="yui-g wikipage-bar">
    <div id="${args.htmlid}-viewButtons" class="yui-u first pageTitle">
      ${page.url.args["title"]?replace("_", " ")}
    </div>
    <div class="yui-u align-right">
    <#assign action = page.url.args["action"]!"view"> 
    <#assign tabs =
	    [
	      { 
	         "id": "#page",
	         "label": msg("tab.view"),
	         "action": "view"
	      },
	      {
	         "id": "#edit",
	         "label": msg("tab.edit"),
	         "action": "edit"
	      },
	      {
	         "id": "#history",
	         "label": msg("tab.details"),
	         "action": "details"
	      }
	    ]>
	    <#list tabs as tab>
 	      <#if tab.action == action>
 	         <span class="tabSelected">${tab.label}</span>
 	      <#else>
 	         <a href="?title=${page.url.args["title"]!""}&action=${tab.action}" class="tabLabel">${tab.label}</a>
 	      </#if>
 	      <#if tab_has_next><span class="separator">|</span></#if>
 	    </#list>
    </div>
</div>  
<div id="${args.htmlid}-wikipage" class="yui-navset">       
	    <div class="yui-content" style="background: #FFFFFF;"> 
<#if action == "view">	    
	        <div id="#page"><#if result.pagetext??>${result.pagetext}<#elseif result.error??>${result.error}</#if></div> 
<#elseif action == "edit">	        
	        <div id="#edit">
	            <form id="${args.htmlid}-form" action="${page.url.context}/proxy/alfresco/slingshot/wiki/page/${page.url.templateArgs.site}/${page.url.args["title"]}" method="POST">
	            <#assign pageContext = page.url.context + "/page/site/" + page.url.templateArgs.site + "/wiki-page?title=" + page.url.args["title"]>
	            <input type="hidden" name="context" value="${pageContext?html}" />
               <textarea name="pagecontent" id="${args.htmlid}-pagecontent" cols="50" rows="10"><#if result.pagetext??>${result.pagetext}</#if></textarea>
               <!-- tags -->
               <#if result.tags?? && result.tags?size &gt; 0>
                  <#assign tags=result.tags />
               <#else>
                  <#assign tags=[] />
               </#if>
               <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
               <!-- Render the tag inputs -->
               <@taglibraryLib.renderTagInputs htmlid=args.htmlid tags=tags tagInputName="tags" />
               <!-- Render the library component -->
               <@taglibraryLib.renderTagLibrary htmlid=args.htmlid site=page.url.templateArgs.site tags=tags />
               <!-- end tags -->
			      <div>
	               <input type="submit" id="${args.htmlid}-save-button" value="Save" />
				      <input type="submit" id="${args.htmlid}-cancel-button" value="Cancel" />
	            </div>
	            </form>
			</div> 
<#elseif action == "details">			
			<div id="#history">
			<div class="yui-gb">
			   <div class="yui-u first">
			      <div class="columnHeader">Version History</div>
			      <#if result.versionhistory??>
			      <#list result.versionhistory as version>
			      <table class="versionHistory">
			         <tr><td colspan="2" class="pageTitle">${version.name}</td></tr>
			         <tr><td class="attributeLabel">Version:</td><td class="attribute">${version.version} (<a href="#" id="${version.versionId}" class="view-link">view</a>)</td></tr>
			         <tr><td class="attributeLabel">Modifier:</td><td class="attribute">${version.author}</td></tr>
			         <tr><td class="attributeLabel">Modified on:</td><td class="attribute">${version.date}</td></tr>
			      </table>
			      </#list>
			      </#if>
			   </div>
			   <div class="yui-u">
			      <div class="columnHeader">Tags</div>
			      <#if result.tags??>
			         <#list result.tags as tag>
			            <div><span class="tagDetails">${tag}</span></div>
			         </#list>
			      </#if>
			   </div>
			   <div class="yui-u">
			      <div class="columnHeader">Linked Pages</div>
			      <#if result.links??>
			         <#list result.links as link>
			            <div><span><a href="${page.url.context}/page/site/${page.url.templateArgs.site}/wiki-page?title=${link?replace(" ", "_")}">${link}</a></span></div>
			         </#list>
			      </#if>
			   </div>
			</div><#-- end of yui-gb -->
			</div>
</#if>
</div> 	    
</div>
