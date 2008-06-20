<div class="dashlet">
   <div class="title">${title!"Activity Feed"}</div>
   <div class="body scrollableList">
	<#if entries?exists && entries?size &gt; 0>
		<#list entries as entry>
		<p>
		<h4>${entry.title}</h4>
		${entry.summary}
		</p>
		</#list>
	<#else>
		<em>${msg("label.noActivities")}</em>
	</#if>
	</div><#-- end of body -->
</div><#-- end of dashlet -->