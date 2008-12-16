
<script type="text/javascript">//<![CDATA[
   new Alfresco.CommentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"links"}",
      height: ${args.editorHeight!180},
      width: ${args.editorWidth!700}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="comment-list" style="display:none;">


   <div class="postlist-infobar">
      <div id="${args.htmlid}-title" class="commentsListTitle"></div>
      <div id="${args.htmlid}-paginator" class="paginator"></div>
   </div>
   <div class="clear"></div>
   <div id="${args.htmlid}-comments"></div>
</div>