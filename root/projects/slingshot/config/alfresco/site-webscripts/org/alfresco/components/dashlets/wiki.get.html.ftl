<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiDashlet("${args.htmlid}").setGUID(
      "${instance.object.id}"
   ).setSiteId("${page.url.templateArgs.site!""}");
//]]></script>
<div class="dashlet">
   <div class="title">Wiki</div>
   <div class="toolbar">
       <a href="#" id="${args.htmlid}-wiki-link">Configure</a>
   </div>
   <div class="body scrollableList" id="${args.htmlid}-scrollableList">
   <#if wikipage?exists>
      ${wikipage}
   <#else>
		<em>No page is configured.</em>
	</#if>
	</div><#-- end of body -->
</div><#-- end of dashlet -->