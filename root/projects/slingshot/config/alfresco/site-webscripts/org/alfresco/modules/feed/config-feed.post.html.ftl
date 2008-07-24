<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>

<#if items?exists && items?size &gt; 0>
	<#list items as item>
	   <@feedLib.renderItem item=item />
	</#list>
<#else>
	<em>No news items.</em>
</#if>