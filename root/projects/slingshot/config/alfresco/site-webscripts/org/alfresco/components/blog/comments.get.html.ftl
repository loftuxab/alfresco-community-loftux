<#import "/org/alfresco/modules/blog/comments.lib.ftl" as commentsLib/>
<#assign postRef=(post.nodeRef?replace("://", "_"))?replace("/", "_")>

<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogComment("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      topicRef: "${post.nodeRef}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<@commentsLib.commentsHTML htmlid=args.htmlid comments=comments/>
