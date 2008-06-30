<#import "/org/alfresco/modules/blog/blogpost.lib.ftl" as blogpostLib/>

<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPost("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args["site"]}",
      mode: "<#if editMode>edit<#else>view</#if>",
      postId: "${page.url.args["postId"]}",
      postRef: "${item.nodeRef}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="discussionsBlogHeader2">
	<div class="leftDiscussionBlogHeader listTitle">
		<span class="backLink">
			<a href="${url.context}/page/blog-postlist?site=${page.url.args.site}">
				${msg("header.back")}
			</a>
		</span>
	</div>
</div>

<div id="${args.htmlid}-post">
   <div id="${args.htmlid}-viewDiv">
      <@blogpostLib.blogpostViewHTML post=item/>
   </div>
</div>
