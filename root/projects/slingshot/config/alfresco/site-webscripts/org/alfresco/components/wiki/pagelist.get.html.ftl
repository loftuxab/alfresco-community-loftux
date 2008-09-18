<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiList("${args.htmlid}").setSiteId(
      "${page.url.templateArgs["site"]!""}"
   );
//]]></script>
<div id="${args.htmlid}-pagelist" class="yui-navset pagelist"> 
<#if pageList.pages?size &gt; 0>
<#list pageList.pages as p>
   <div class="wikipage <#if p.tags??><#list p.tags as t>wp-${t}<#if t_has_next> </#if></#list></#if>">
   <div class="actionPanel">
      <div class="editPage"><a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki-page?title=${p.name?url}&amp;action=edit">${msg("link.edit")}</a></div>
      <div class="detailsPage"><a href="${url.context}/page/site/${page.url.templateArgs.site}/wiki-page?title=${p.name?url}&amp;action=details">${msg("link.details")}</a></div>
      <div class="deletePage"><a href="#" class="delete-link" title="${p.name}">${msg("link.delete")}</a></div>
   </div>
   <div class="pageTitle"><a class="pageTitle" href="${url.context}/page/site/${page.url.templateArgs.site}/wiki-page?title=${p.name?url}">${p.title}</a></div>
   <div class="publishedDetails">
      <span class="attrLabel">${msg("label.creator")}</span> <span class="attrValue"><a href="${url.context}/user/${p.createdByUser?url}/profile">${p.createdBy}</a></span>
		<span class="spacer">&nbsp;</span>
		<span class="attrLabel">${msg("label.createDate")}</span> <span class="attrValue">${p.createdOn}</span>
		<span class="spacer">&nbsp;</span>
		<span class="attrLabel">${msg("label.modifier")}</span> <span class="attrValue"><a href="${url.context}/user/${p.modifiedByUser?url}/profile">${p.modifiedBy}</a></span>
		<span class="spacer">&nbsp;</span><br/>
		<span class="attrLabel">${msg("label.modifiedDate")}</span> <span class="attrValue">${p.modifiedOn}</span>
	</div>
	<#assign pageCopy>${p.text!""}</#assign>
   <div class="pageCopy"><#if pageCopy?length &lt; 1000>${pageCopy}<#else>${pageCopy?substring(0, 1000)}...</#if></div>
   <#-- Display tags, if any -->
   <div class="pageTags">
      <span class="tagDetails">${msg("label.tags")}</span>
      <#if p.tags?? && p.tags?size &gt; 0><#list p.tags as tag><span>${tag}</span><#if tag_has_next>,&nbsp;</#if></#list><#else>${msg("label.none")}</#if>
   </div>
   </div><#-- End of wikipage -->
</#list>
<#else>
${msg("lable.noPages")}
</#if>
</div>