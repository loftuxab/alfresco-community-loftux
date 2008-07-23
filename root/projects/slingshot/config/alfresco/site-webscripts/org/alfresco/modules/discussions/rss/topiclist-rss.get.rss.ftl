<?xml version="1.0"?>
<rss version="2.0">
	<channel>
		<title>${msg("topiclistrss.title")}</title>
		<link>${absurl(url.context)}/service/components/discussions/rss?site=${site}&amp;container=${container}</link>
		<description>${msg("topiclistrss.description")}</description>
		<language>${lang}</language>

		<#if (items?size > 0)>
			<#list items as topic>
			      <item>
			         <title>${topic.title?html}</title>
			         <link>${absurl(url.context)}/page/site/${site}/discussions-topicview?container=${container}&amp;topicId=${topic.name}</link>
			         <description>${topic.content?html}</description>
                     <#-- make sure we use en_US for date rendering -->
                     <#assign currentLocale=locale />
                     <#setting locale="en_US" />
                     <pubDate>${topic.createdOn?datetime?string("EEE, dd MMM yyyy HH:mm:ss Z")}</pubDate>
                     <#setting locale=currentLocale />
			      </item>
			</#list>
		<#else>
		      <item>${msg("topiclistrss.nocontent")}</item>
		</#if>
	</channel>
</rss>
