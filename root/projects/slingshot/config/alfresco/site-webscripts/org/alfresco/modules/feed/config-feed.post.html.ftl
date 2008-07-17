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