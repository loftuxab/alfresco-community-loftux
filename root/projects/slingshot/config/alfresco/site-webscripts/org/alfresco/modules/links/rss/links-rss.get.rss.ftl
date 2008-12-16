<?xml version="1.0"?>
<rss version="2.0">
   <channel>
      <title>${msg("linksrss.title")}</title>
      <link>${absurl(url.context)}/service/components/links/rss?site=${site}&amp;container=${container}</link>
      <description>${msg("linksrss.description")}</description>
      <language>${lang}</language>

      <#if (items?size > 0)>
         <#list items as link>
            <item>
               <title>${link.title?html}</title>
               <link>${absurl(url.context)}/page/site/${site}/links-view?container=${container}&amp;linkId=${link.name}</link>
               <description>${link.description?html}</description>
            </item>
         </#list>
      <#else>
         <item><title>${msg("linksrss.noposts")}</title></item>
      </#if>
   </channel>
</rss>