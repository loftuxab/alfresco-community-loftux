
<script type="text/javascript">//<![CDATA[
   new Alfresco.CommentList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${args.container!"blog"}",
      height: ${args.editorHeight!180},
      width: ${args.editorWidth!700}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="comment-list" style="display:none;">

   <div class="postlist-infobar yui-gd">
      <div class="yui-u first">
         <div id="${args.htmlid}-title" class="commentsListTitle"></div>
      </div>
      <div class="yui-u align-right">
         <div id="${args.htmlid}-paginator" class="paginator"></div>
      </div>
   </div>
   <div id="${args.htmlid}-comments"></div>
</div>