<script type="text/javascript">//<![CDATA[
   new Alfresco.RssFeed("${args.htmlid}").setGUID(
      "${instance.object.id}"
   );
//]]></script>
<div class="dashlet">
   <div class="title">${title!""}</div>
   <div class="toolbar">
       <a href="#" id="${args.htmlid}-configFeed-link">Configure</a>
   </div>
   <div class="body scrollableList" id="${args.htmlid}-scrollableList">
	<#if items?exists && items?size &gt; 0>
		<#list items as i>
		<p>
		<h4><a href="${i.link}">${i.title}</a></h4>
		${i.description}
		</p>
		</#list>
	<#else>
		<em>No news items.</em>
	</#if>
	</div><#-- end of body -->
</div><#-- end of dashlet -->
