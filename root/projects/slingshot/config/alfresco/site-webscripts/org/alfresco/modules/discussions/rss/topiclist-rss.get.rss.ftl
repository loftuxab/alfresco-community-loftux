<?xml version="1.0"?>
<rss version="2.0">
	<channel>
		<title>Alfresco Discussions Topics</title>
		<link>${absurl(url.context)}/service/components/discussions/rss?site=${site}&amp;container=${container}</link>
		<description>RSS feed of the discussions</description>
		<language>${lang}</language>

		<#if (items?size > 0)>
			<#list items as topic>		
			      <item>
			         <title>${topic.title?html}</title>
			         <link>${absurl(url.context)}/page/discussions-topicview?site=${site}&amp;container=${container}&amp;topicId=${topic.name}</link>
			         <description>${topic.content?html}</description>
			         <pubDate>${topic.createdOn?datetime?string("MMM dd yyyy HH:mm:ss 'GMT'Z '('zzz')'")}</pubDate>
			      </item>
			</#list>
		<#else>
		      <item>No topic content yet</item>
		</#if>
	</channel>
</rss>
