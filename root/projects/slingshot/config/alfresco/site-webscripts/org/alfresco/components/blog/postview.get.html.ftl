<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostView("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site}",
      containerId: "blog",
      postId: "${page.url.args['postId']}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="postview-header">
	<div class="back-nav">
		<span class="backLink">
			<a href="${url.context}/page/site/${page.url.templateArgs.site}/blog-postlist">
				${msg("header.back")}
			</a>
		</span>
	</div>
</div>

<div id="${args.htmlid}-post">
   <div id="${args.htmlid}-post-view-div">
   </div>
</div>
