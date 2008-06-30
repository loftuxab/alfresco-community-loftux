<#import "/org/alfresco/modules/blog/comments.lib.ftl" as commentsLib/>

<script type="text/javascript">//<![CDATA[
   new Alfresco.CreateComment("${args.htmlid}").setOptions(
   {
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="submitCommentForm">
	<div class="commentFormTitle">
		${msg("comments.addComment")}
	</div>
	<div class="editComment">
		<form id="${htmlid}-createcomment-form" name="${htmlid}-createcomment-form" method="POST"
            action="${page.url.context}/proxy/alfresco/node/${nodeRef?replace('://','/')}/comments" 
		>
			<input type="hidden" name="nodeRef" value="${nodeRef}" />
			<input type="hidden" name="htmlid" value="${htmlid}" />
			<textarea id="${htmlid}-createcomment-content" rows="8" cols="80" name="content"></textarea>
			<div class="commentFormAction">
				<input type="submit" id="${htmlid}-createcomment-ok-button" value="${msg('comments.form.postComment')}" />
			</div>
		</form>
	</div>
</div>