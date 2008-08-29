<script type="text/javascript">//<![CDATA[
	new Alfresco.Wiki("${args.htmlid}").setOptions({
	   "siteId": "${page.url.templateArgs.site}",
	   "pageTitle": "${page.url.args["title"]!""}",
	   "mode": "${page.url.args["action"]!"view"}"
	}).setMessages(
      ${messages}
   );
//]]></script>
<div class="yui-g wikipage-bar">
<div class="wikipage-header">
	<div class="back-nav">
		<span class="backLink">
			<a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki">
				${msg("header.back")}
			</a>
		</span>
	</div>
</div>
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
	            <form id="${args.htmlid}-form" action="${page.url.context}/proxy/alfresco/slingshot/wiki/page/${page.url.templateArgs.site}/${page.url.args["title"]}" method="post">
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
	               <input type="submit" id="${args.htmlid}-save-button" value="${msg("button.save")}" />
				      <input type="submit" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" />
	            </div>
	            </form>
			</div> 
<#elseif action == "details">	    		
			<div id="#history">
			<div style="border: 3px solid #CCC; margin-bottom:15px; width:100%">
			<div class="yui-g" style="background: #CCC;">
			   <div class="yui-u first"><h2>${result.title!""}</h2></div>
			   <div class="yui-u">
			      <div style="float:right">
			      <select id="${args.htmlid}-selectVersion">
			      <#list result.versionhistory as version>
			         <option value="${version.versionId}">${version.version} <#if version_index = 0>(Latest)</#if></option>
			      </#list>
			      </select>
			      </div>
			   </div>
			</div>
			<div id="#page">
			   <#-- PAGE CONTENT GOES HERE -->
			   <#if result.pagetext??>${result.pagetext}</#if>
			</div>
			<div id="${args.htmlid}-pagecontent" style="display:none;"><#if result.pagetext??>${result.pagetext}</#if></div>		
			</div>
			 <div style="display:none; margin-bottom: 5px;" id="${args.htmlid}-revertPanel"><button id="${args.htmlid}-revert-button">${msg("button.revert")}</button></div>
			<div class="yui-gb">
			   <div class="yui-u first">
			      <div class="columnHeader">${msg("label.versionHistory")}</div>
			      <#if result.versionhistory??>
			      <#list result.versionhistory as version>
			      <table class="versionHistory">
			         <tr><td colspan="2" class="pageTitle">${version.name}</td></tr>
			         <tr><td class="attributeLabel">${msg("label.version")}:</td><td class="attribute">${version.version}</td></tr>
			         <tr><td class="attributeLabel">${msg("label.modifier")}:</td><td class="attribute">${version.author}</td></tr>
			         <tr><td class="attributeLabel">${msg("label.modifiedOn")}:</td><td class="attribute">${version.date}</td></tr>
			      </table>
			      </#list>
			      </#if>
			   </div>
			   <div class="yui-u">
			      <div class="columnHeader">${msg("label.tags")}</div>
			      <#if result.tags??>
			         <#list result.tags as tag>
			            <div><span class="tagDetails">${tag}</span></div>
			         </#list>
			      </#if>
			   </div>
			   <div class="yui-u">
			      <div class="columnHeader">${msg("label.linkedPages")}</div>
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
