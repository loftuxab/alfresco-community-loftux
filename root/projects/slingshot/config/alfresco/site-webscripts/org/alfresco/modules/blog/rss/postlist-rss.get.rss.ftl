<?xml version="1.0"?>
<rss version="2.0">
   <channel>
      <title>${msg("postlistrss.title")}</title>
      <link>${absurl(url.context)}/service/components/blog/rss?site=${site}&amp;container=${container}</link>
      <description>${msg("postlistrss.description")}</description>
      <language>${lang}</language>

      <#if (items?size > 0)>
         <#list items as post>
            <item>
               <title><#if post.isDraft>${msg("postlistrss.draft")}: </#if>${post.title?html}</title>
               <link>${absurl(url.context)}/page/site/${site}/blog-postview?container=${container}&amp;postId=${post.name}</link>
               <description>${post.content?html}</description>
               <#if (! post.isDraft)>
                  <pubDate>${post.releasedOn?datetime?string("MMM dd yyyy HH:mm:ss 'GMT'Z '('zzz')'")}</pubDate>
               </#if>
            </item>
         </#list>
      <#else>
         <item>${msg("postlistrss.noposts")}</item>
      </#if>
   </channel>
</rss>