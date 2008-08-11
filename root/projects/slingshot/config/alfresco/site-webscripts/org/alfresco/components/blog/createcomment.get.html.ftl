<script type="text/javascript">//<![CDATA[
   new Alfresco.CreateComment("${args.htmlid}").setOptions(
   {
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-form-container" class="addCommentForm hidden">
	<div class="commentFormTitle">
		${msg("comments.addComment")}
	</div>
	<div class="editComment">
		<form id="${htmlid}-form" method="post" action="">
		    <div>
			<input type="hidden" id="${args.htmlid}-nodeRef" name="nodeRef" value="" />
            <input type="hidden" id="${args.htmlid}-site" name="site" value="" />
            <input type="hidden" id="${args.htmlid}-container" name="container" value="" />
            <input type="hidden" id="${args.htmlid}-itemTitle" name="itemTitle" value="" />
            <input type="hidden" id="${args.htmlid}-browseItemUrl" name="browseItemUrl" value="" />
            
			<textarea id="${htmlid}-content" rows="8" cols="80" name="content"></textarea>
			</div>
			<div class="commentFormAction">
				<input type="submit" id="${htmlid}-submit" value="${msg('comments.form.postComment')}" />
			</div>
		</form>
	</div>
</div>