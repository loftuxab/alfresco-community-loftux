<div class="dashlet">
   <div class="title">${title!"Activity Feed"}</div>
   <div class="body scrollableList">
	<#if entries?exists && entries?size &gt; 0>
		<#list entries as entry>
	   <div class="detail-list-item">
   		<div>
   		   <h4>${entry.title}</h4>
   		   ${entry.summary}
   		</div>
	   </div>
		</#list>
	<#else>
		${msg("label.noActivities")}
	</#if>
	</div><#-- end of body -->
</div><#-- end of dashlet -->
<script type="text/javascript">//<![CDATA[
(function()
{
   var links = YAHOO.util.Selector.query("a[rel]", "${args.htmlid}");
   for (var i = 0, len = links.length; i < len; ++i)
   {
      links[i].setAttribute("target", links[i].getAttribute("rel"));
   }
})();
//]]></script>