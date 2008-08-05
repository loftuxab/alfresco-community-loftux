<#if showComponent>
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
		<form id="${htmlid}-createcomment-form" method="post" action="${page.url.context}/proxy/alfresco/api/node/${nodeRef?replace('://','/')}/comments">
		    <div>
			<input type="hidden" name="nodeRef" value="${nodeRef}" />
			<input type="hidden" name="htmlid" value="${htmlid}" />
            <input type="hidden" name="site" value="${site}" />
            <input type="hidden" name="container" value="${container}" />
            <input type="hidden" name="itemTitle" value="${itemTitle?html}" />
            <input type="hidden" name="browseItemUrl" value="${page.url.context}/page/site/${site}/blog-postview?postId=${itemName?html}" />
            
			<textarea id="${htmlid}-createcomment-content" rows="8" cols="80" name="content"></textarea>
			</div>
			<div class="commentFormAction">
				<input type="submit" id="${htmlid}-createcomment-ok-button" value="${msg('comments.form.postComment')}" />
			</div>
		</form>
	</div>
</div>
</#if>