<?xml version="1.0"?>
<rss version="2.0">
	<channel>
		<title>Alfresco Discussions Topics</title>
		<link>${url.context}/</link>
		<description>RSS feed of the discussions</description>
		<language>${lang}</language>

		<#if (items?size > 0)>
			<#list items as topic>		
			      <item>
			         <title>${topic.title?html}</title>
			         <link>${host}${topic.url}</link>
			         <description>${topic.content?html}</description>
			         <pubDate>${topic.createdOn?datetime?string.long_long}</pubDate>
			      </item>
			</#list>
		<#else>
		      <item>No topic content yet</item>
		</#if>
	</channel>
</rss>
