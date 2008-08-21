
<script type="text/javascript">//<![CDATA[
   new Alfresco.CommentList("${args.htmlid}").setOptions(
   {
      height: ${args.editorHeight!180},
      width: ${args.editorWidth!700}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="comment-list" style="display:none;">
   <div id="${args.htmlid}-title" class="commentsListTitle"></div>
   <div id="${args.htmlid}-comments"></div>
</div>