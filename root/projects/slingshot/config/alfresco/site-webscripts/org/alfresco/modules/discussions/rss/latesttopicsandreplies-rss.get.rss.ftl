<?xml version="1.0"?>
<rss version="2.0">
	<channel>
		<title>Alfresco Discussions Topics</title>
		<link>${host}/</link>
		<description>RSS feed of the discussions</description>
		<language>${lang}</language>

		<#if (items?size > 0)>
			<#list items as postdata>		
			      <item>
			         <title>${postdata.topicTitle?html}</title>
			         <link>${host}/${postdata.url}</link>
			         <description>${postdata.content?html}</description>
			         <pubDate>${postdata.createdOn}</pubDate>
			      </item>
			</#list>
		<#else>
		      <item>No topic content yet</item>
		</#if>
	</channel>
</rss>
