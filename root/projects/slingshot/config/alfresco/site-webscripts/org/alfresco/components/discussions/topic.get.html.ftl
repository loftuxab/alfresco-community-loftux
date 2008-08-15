<#assign container = args.container!'discussions' />

<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsTopic("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "${container}",
      topicId: "${page.url.args.topicId!''}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="topicview-header">
   <div class="left-aligned-cell listTitle">
      <span class="backLink">
         <#-- PENDING: should be generated to correctly handle different containers -->
         <a href="${url.context}/page/site/${page.url.templateArgs.site}/discussions-topiclist?container=${container}">
            ${msg("header.back")}
         </a>
      </span>
   </div>
</div>

<div id="${args.htmlid}-topic">
   <div id="${args.htmlid}-topic-view-div">
   </div>
   <div id="${args.htmlid}-topic-edit-div">
   </div>
</div>
