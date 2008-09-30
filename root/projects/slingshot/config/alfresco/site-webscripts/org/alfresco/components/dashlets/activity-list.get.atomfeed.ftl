<#if args["mode"] = "user">
   <#assign title=msg("atom.title.user", user.fullName?xml)>
<#else>
   <#assign title=msg("atom.title.site", args["site"]?xml)>
</#if>
<#assign genericTitle=msg("title.generic")>
<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom">
   <generator version="1.0">Alfresco (1.0)</generator>
   <link rel="self" href="${absurl(url.full)?xml}" />
   <id>${absurl(url.full)?xml}</id>
   <title>${title}</title>
<#if activities?exists && activities?size &gt; 0>
   <updated>${activities[0].date.isoDate}</updated>
   <#list activities as activity>
      <#assign userLink="<a href=\"${activity.userProfile}\">${activity.fullName}</a>">
      <#assign siteLink="<a href=\"${activity.sitePage}\">${activity.title}</a>">
   <entry xmlns='http://www.w3.org/2005/Atom'>
      <title>${activity.title!genericTitle}</title>
      <link rel="alternate" type="text/html" href="${absurl(activity.sitePage)}" />
      <id>${absurl(activity.sitePage)}</id>
      <updated>${activity.date.isoDate}</updated>
      <summary type="html">
         <![CDATA[${msg(activity.type, siteLink, userLink, activity.custom0, activity.custom1)}]]>
      </summary>
      <author>
         <name>${activity.fullName!""}</name>
         <uri>${absurl(activity.userProfile)}</uri>
      </author> 
   </entry>
   </#list>
</#if>
</feed>
