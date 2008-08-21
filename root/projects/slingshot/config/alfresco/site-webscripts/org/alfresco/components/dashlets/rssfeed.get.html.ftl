<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>
<#assign DISPLAY_ITEMS = 999>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RssFeed("${args.htmlid}").setConfigOptions(
   {
      "componentId": "${instance.object.id}",
      "feedURL": "${uri}", 
      "limit": "<#if limit?number != DISPLAY_ITEMS>${limit}<#else>all</#if>"      
   });
//]]></script>
<div class="dashlet">
   <div class="title">${title!""}</div>
   <div class="toolbar">
       <a href="#" id="${args.htmlid}-configFeed-link">${msg("label.configure")}</a>
   </div>
   <div class="body scrollableList" id="${args.htmlid}-scrollableList">
	<#if items?exists && items?size &gt; 0>
		<#list items as item>
		   <#if item_index &lt; limit?number><@feedLib.renderItem item=item target=target/><#else><#break></#if>
		</#list>
	<#else>
		<em>${msg("label.no_items")}.</em>
	</#if>
	</div><#-- end of body -->
</div><#-- end of dashlet -->
