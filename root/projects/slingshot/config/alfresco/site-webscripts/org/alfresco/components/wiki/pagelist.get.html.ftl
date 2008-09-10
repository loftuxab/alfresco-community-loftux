<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiList("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   );
//]]></script>
<div id="${args.htmlid}-pagelist" class="yui-navset"> 
<#if pageList.pages?size &gt; 0>
<#list pageList.pages as p>
   <div class="wikipage <#if p.tags??><#list p.tags as t>wp-${t}<#if t_has_next> </#if></#list></#if>">
   <div class="actionPanel">
      <div class="editPage"><a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki-page?title=${p.name?url}&amp;action=edit">Edit</a></div>
      <div class="detailsPage"><a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki-page?title=${p.name?url}&amp;action=details">Details</a></div>
      <div class="deletePage"><a href="#" class="delete-link" title="${p.name}">Delete</a></div>
   </div>
   <div class="pageTitle"><a class="pageTitle" href="${url.context}/page/site/${page.url.templateArgs.site}/wiki-page?title=${p.name?url}">${p.title}</a></div>
   <div class="publishedDetails">
      <!-- http://localhost:8081/share/page/user/mikeh/profile -->
      <span class="attrLabel">Created by:</span> <span class="attrValue"><a href="${url.context}/user/${p.createdByUser?url}/profile">${p.createdBy}</a></span>
		<span class="spacer">&nbsp;</span>
		<span class="attrLabel">Created on:</span> <span class="attrValue">${p.createdOn}</span>
		<span class="spacer">&nbsp;</span>
		<span class="attrLabel">Modified by:</span> <span class="attrValue"><a href="${url.context}/user/${p.modifiedByUser?url}/profile">${p.modifiedBy}</a></span>
		<span class="spacer">&nbsp;</span><br/>
		<span class="attrLabel">Modified on:</span> <span class="attrValue">${p.modifiedOn}</span>
	</div>
	<#assign pageCopy>${p.text!""}</#assign>
   <div class="pageCopy"><#if pageCopy?length &lt; 1000>${pageCopy}<#else>${pageCopy?substring(0, 1000)}...</#if></div>
   <#-- Display tags, if any -->
   <div class="pageTags">
      <span class="tagDetails">Tags:</span>
      <#if p.tags??><#list p.tags as tag><span>${tag}</span><#if tag_has_next>,&nbsp;</#if></#list></#if>
   </div>
   </div><#-- End of wikipage -->
</#list>
<#else>
There are currently no pages to display.
</#if>
</div>