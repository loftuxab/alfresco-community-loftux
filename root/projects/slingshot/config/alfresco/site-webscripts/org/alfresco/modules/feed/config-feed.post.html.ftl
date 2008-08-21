<#import "/org/alfresco/utils/feed.utils.ftl" as feedLib/>

<#if items?exists && items?size &gt; 0>
	<#list items as item>
	   <#if item_index &lt; limit?number><@feedLib.renderItem item=item target=target/><#else><#break></#if>
	</#list>
<#else>
	<em>No news items.</em>
</#if>