<#import "/org/alfresco/modules/blog/blogpostlist.lib.ftl" as blogpostlistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>

<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args["site"]!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<script type="text/javascript">//<![CDATA[
   new Alfresco.ConfigBlog("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args["site"]!""}"
   }).setMessages(
      ${messages}
   );;
//]]></script>

<div id="discussionsBlogHeader1">
	<div class="leftDiscussionBlogHeader">
		<span class="createNodeLink">
		   <a href="${url.context}/page/blog-postedit?site=${page.url.args["site"]}">
		      ${msg("header.createPost")}
		   </a>
		</span>
		<span class="rssLink">
		   <a href="${url.context}/service/components/blog/rss?site=${page.url.args["site"]}">
	          ${msg("header.blogRSS")}
	       </a>
		</span>
		
		<span class="configLink">
		   <a id="${args.htmlid}-config-blog-button" href="#">
	          Blog config
	       </a>
		</span>
	</div>
	<div class="rightDiscussionBlogHeader">
		<span class="toolbarOption detailList"><a href="#" id="detailed-list-view">${msg("header.detailList")}</a></span>
		<span class="toolbarOption simpleList"><a href="#" id="simple-list-view">${msg("header.simpleList")}</a></span>
	</div>
</div>

<div id="discussionsBlogHeader2">
	<div id="${args.htmlid}-listtitle" class="leftDiscussionBlogHeader listTitle">
			${listTitle}
	</div>
	
	<div class="rightDiscussionBlogHeader">
		<div id="${args.htmlid}-paginator" class="toolbarOption">
			<@paginatorLib.renderForumPaginator pdata=pdata />
		</div>
	</div>
	
</div>

<div id="${args.htmlid}-postlist">
<@blogpostlistLib.postListHTML posts=items viewmode=viewmode />
</div>