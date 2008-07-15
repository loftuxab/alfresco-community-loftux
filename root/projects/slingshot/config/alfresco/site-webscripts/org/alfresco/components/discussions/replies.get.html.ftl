<#import "/org/alfresco/modules/discussions/replies.lib.ftl" as repliesLib/>
<#assign topicRef=(topic.nodeRef?replace("://", "_"))?replace("/", "_")>

<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsReplies("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "${container}",
      path: "${path}",
      topicRef: "${topicRef}"
   }).setMessages(
      ${messages}
   );
//]]></script>
 

<@repliesLib.repliesHTML htmlid=args.htmlid parentPostRef=topicRef replies=replies/>
