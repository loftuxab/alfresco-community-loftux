<#-- Tags -->
<#if result.tags?? && result.tags?size &gt; 0>
   <#assign tags=result.tags />
<#else>
   <#assign tags=[] />
</#if>
   
<script type="text/javascript">//<![CDATA[
	new Alfresco.Wiki("${args.htmlid}").setOptions(
	{
	   siteId: "${page.url.templateArgs.site}",
      pageTitle: "${page.url.args["title"]!""}",
	   mode: "${page.url.args["action"]!"view"}",
	   tags: [<#list tags as tag>"${tag}"<#if tag_has_next>,</#if></#list>],
      pages: [<#if pageList.pages?size &gt; 0><#list pageList.pages as p>"${p.name}"<#if p_has_next>, </#if></#list></#if>],
      versions: [
      <#if result.versionhistory??>
         <#list result.versionhistory as version>
         {
            title: '${version.name}',
            label: '${version.version}',
            versionId: '${version.versionId}',
            createdDate: '${version.date}'
         }<#if (version_has_next)>,</#if>
         </#list>
      </#if>              
      ]
	}).setMessages(
      ${messages}
   );    
//]]></script>
<#-- Note, since result.pagetext has already been stripped by the page.get.js script -->
<div class="yui-g wikipage-bar">

   <div class="title-bar">
      <div id="${args.htmlid}-viewButtons" class="yui-u first pageTitle">
         ${page.url.args["title"]?replace("_", " ")}
      </div>
      <div class="yui-u align-right">
<#assign action = page.url.args["action"]!"view"> 
<#assign tabs =
[
   { 
      "label": msg("tab.view"),
      "action": "view"
   },
   {
      "label": msg("tab.edit"),
      "action": "edit"
   },
   {
      "label": msg("tab.details"),
      "action": "details"
   }
]>
<#list tabs as tab>
   <#if tab.action == action>
         <span class="tabSelected">${tab.label}</span>
   <#else>
         <a href="?title=${page.url.args["title"]!""}&amp;action=${tab.action}" class="tabLabel">${tab.label}</a>
   </#if>
   <#if tab_has_next>
         <span class="separator">|</span>
   </#if>
</#list>
      </div>
   </div>
</div>  
<div id="${args.htmlid}-wikipage" class="wiki-page">       
	    <div class="yui-content" style="background: #FFFFFF;"> 
<#if action == "view">	    
	        <div id="${args.htmlid}-page" class="rich-content"><#if result.pagetext??>${result.pagetext}<#elseif result.error??>${result.error}</#if></div> 
<#elseif action == "edit">	        
           <div>
	           <form id="${args.htmlid}-form" action="${page.url.context}/proxy/alfresco/slingshot/wiki/page/${page.url.templateArgs.site}/${page.url.args["title"]}" method="post">
	              <fieldset>
                    <#assign pageContext = page.url.context + "/page/site/" + page.url.templateArgs.site + "/wiki-page?title=" + page.url.args["title"]>
                       <input type="hidden" name="context" value="${pageContext?html}" />
                       <input type="hidden" name="page" value="wiki-page" />
                       <label for="${htmlid}-pagecontent">${msg("label.text")}:</label>
                       <textarea name="pagecontent" id="${args.htmlid}-pagecontent" cols="50" rows="10"><#if result.pagetext??>${result.pagetext}</#if></textarea>
                       <label for="${htmlid}-tag-input-field">${msg("label.tags")}:</label>
                       <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
               
                       <!-- Render the tag inputs -->
                       <@taglibraryLib.renderTagLibraryHTML htmlid=args.htmlid />
                       <!-- end tags -->
                       <div class="buttons">
                          <input type="submit" id="${args.htmlid}-save-button" value="${msg("button.save")}" />
                          <input type="submit" id="${args.htmlid}-cancel-button" value="${msg("button.cancel")}" />
                       </div>
                 </fieldset>
              </form>
           </div>
<#elseif action == "details">	    		
			<div>
   			<div class="details-wrapper">
   			<div class="yui-g">
			      <div class="yui-u first">
                  <h2>
                     ${result.title!""}
                     <#if result.versionhistory??><#list result.versionhistory as version><#if version_index == 0><span id="${args.htmlid}-version-header" class="light">${msg("label.shortVersion")}${version.version}</span></#if></#list></#if>
                  </h2>
               </div>
			      <div class="yui-u">
			   	<#if result.versionhistory??>
                  <div class="version-quick-change">
				      <#list result.versionhistory as version>
                  <#if version_index == 0>
                  <input type="button" id="${args.htmlid}-selectVersion-button" name="selectButton" value="${version.version} (${msg("label.latest")})">
				      <select id="${args.htmlid}-selectVersion-menu" name="selectVersion">
                  </#if>
				         <option value="${version.versionId}">${version.version} <#if version_index = 0>(${msg("label.latest")})<#else>(${msg("label.earlier")})</#if></option>
				      </#list>
				      </select>
                  </div>
                  <div class="version-quick-change">${msg("label.viewVersion")}</div>
			      </#if>
			   </div>
			</div>
			<div id="${args.htmlid}-page" class="details-page-content">
			   <#-- PAGE CONTENT GOES HERE -->
			   <#if result.pagetext??>${result.pagetext}</#if>
			</div>
         <!--
			<div id="${args.htmlid}-pagecontent" style="display:none;"><#if result.pagetext??>${result.pagetext}</#if></div>
		   -->
			</div>
         <!--
			<div style="display:none; margin-bottom: 5px;" id="${args.htmlid}-revertPanel"><button id="${args.htmlid}-revert-button">${msg("button.revert")}</button></div>
			-->
			<div class="yui-gb">
			   <div class="yui-u first">
               <div class="columnHeader">${msg("label.versionHistory")}</div>
               <#if result.versionhistory??>
               <#list result.versionhistory as version>
                  <#if version_index == 0>
                     <div class="info-sub-section">
                        <span class="meta-heading">${msg("section.thisVersion")}</span>
                     </div>
                  </#if>
                  <#if version_index == 1>
                     <div class="info-sub-section">
                        <span class="meta-heading">${msg("section.olderVersion")}</span>
                     </div>
                  </#if>
                  <div id="${args.htmlid}-expand-div-${version_index}" class="info more <#if version_index != 0>collapsed<#else>expanded</#if>">
                     <span class="meta-section-label">${msg("label.version")} ${version.version}</span>
                     <span id="${args.htmlid}-createdDate-span-${version_index}" class="meta-value">&nbsp;</span>
                  </div>
                  <div id="${args.htmlid}-moreVersionInfo-div-${version_index}" class="moreInfo" <#if version_index != 0>style="display: none;"</#if>>
                     <div class="info">
                        <span class="meta-label">${msg("label.title")}</span>
                        <span class="meta-value">${version.name?html}</span>
                     </div>
                     <div class="info">
                        <span class="meta-label">${msg("label.creator")}</span>
                        <span class="meta-value">${version.author?html}</span>
                     </div>
                     <#if version_index != 0>
                     <div class="actions">
                           <span id="${args.htmlid}-revert-span-${version_index}" class="revert"><a>${msg("link.revert")}</a></span>
                     </div>
                     </#if>
                  </div>
               </#list>
               </#if>
			   </div>
			   <div class="yui-u">
			      <div class="columnHeader">${msg("label.tags")}</div>
               <div class="tags">
			      <#if result.tags?? && result.tags?size &gt; 0>
			         <#list result.tags as tag>
                     <div class="tag"><img src="${page.url.context}/components/images/tag-16.png" /> ${tag}</img></div>			           
			         </#list>
               <#else>
                  ${msg("label.none")}
               </#if>
               </div>
			   </div>
			   <div class="yui-u">
			      <div class="columnHeader">${msg("label.linkedPages")}</div>
               <div class="links">               
			      <#if result.links??>
			         <#list result.links as link>
			            <div><span><a href="${page.url.context}/page/site/${page.url.templateArgs.site}/wiki-page?title=${link?replace(" ", "_")}">${link}</a></span></div>
			         </#list>
			      </#if>
               </div>
			   </div>
			</div><#-- end of yui-gb -->
			</div>
</#if>
</div> 	    
</div>
