<script type="text/javascript">//<![CDATA[
   new Alfresco.WikiDashlet("${args.htmlid}").setGUID(
      "${instance.object.id}"
   ).setSiteId("${page.url.templateArgs.site!""}");
//]]></script>
<div class="dashlet">
   <div class="title" id="${args.htmlid}-title">${pageTitle!msg("label.header")}</div>
   <div class="toolbar">
       <a href="#" id="${args.htmlid}-wiki-link">${msg("label.configure")}</a>
   </div>
   <div class="body scrollableList" id="${args.htmlid}-scrollableList">
   <#if wikipage?exists>
      ${wikipage}
   <#else>
		${msg("label.noConfig")}
	</#if>
	</div><#-- end of body -->
</div><#-- end of dashlet -->