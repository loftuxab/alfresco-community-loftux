<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>
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
		<#list items as item>
         <@feedLib.renderItem item=item />
		</#list>
	<#else>
		<em>No news items.</em>
	</#if>
	</div><#-- end of body -->
</div><#-- end of dashlet -->
