<?xml version="1.0"?>
<rss version="2.0">
	<channel>
		<title>Blog Posts</title>
		<link>${absurl(url.context)}/service/components/blog/rss?site=${site}&amp;container=${container}</link>
		<description>RSS feed of the blog posts</description>
		<language>${lang}</language>

		<#if (items?size > 0)>
			<#list items as post>
			      <item>
			         <title>${post.title?html}</title>
			         <link>${absurl(url.context)}/page/blog-postview?site=${site}&amp;container=${container}&amp;postId=${post.name}</link>
			         <description>${post.content?html}</description>
			         <pubDate>${post.createdOn?datetime?string("MMM dd yyyy HH:mm:ss 'GMT'Z '('zzz')'")}</pubDate>
			      </item>
			</#list>
		<#else>
		      <item>No blog posts created yet</item>
		</#if>
	</channel>
</rss>
