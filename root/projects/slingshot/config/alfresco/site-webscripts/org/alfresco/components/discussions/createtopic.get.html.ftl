<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsTopic("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      mode: "create"
   }).setMessages(
      ${messages}
   );
//]]></script>

<#import "/org/alfresco/modules/discussions/topic.lib.ftl" as topicLib/>

<div id="${args.htmlid}-topic">
   <div id="${args.htmlid}-viewDiv">
   </div>
   <div id="${args.htmlid}-formDiv">
     <@topicLib.topicFormHTML htmlid="${args.htmlid}"/>
   </div>
</div>