<?xml version="1.0"?>
<rss version="2.0">
	<channel>
		<title>Alfresco Discussions Topics</title>
		<link>${absurl(url.context)}/service/components/discussions/rss/latestposts?site=${site}&amp;container=${container}</link>
		<description>RSS feed of the discussions</description>
		<language>${lang}</language>

		<#if (items?size > 0)>
			<#list items as postdata>		
			      <item>
			         <title><#if postdata.isRootPost>New topic:<#else>Reply to topic:</#if> ${postdata.topicTitle?html}</title>
			         <link>${absurl(url.context)}/page/site/${site}/discussions-topicview?container=${container}&amp;topicId=${postdata.topicName}</link>
			         <description>${postdata.content?html}</description>
			         <pubDate>${postdata.createdOn?datetime?string("MMM dd yyyy HH:mm:ss 'GMT'Z '('zzz')'")}</pubDate>
			      </item>
			</#list>
		<#else>
		      <item>No topic content yet</item>
		</#if>
	</channel>
</rss>
