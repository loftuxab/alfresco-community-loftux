<#import "/org/alfresco/modules/blog/blogpostlist.lib.ftl" as blogpostlistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>

<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      startIndex: "${pdata.startIndex}",
      pageSize: "${pdata.pageSize}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<script type="text/javascript">//<![CDATA[
   new Alfresco.ConfigBlog("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );;
//]]></script>

<div id="discussionsBlogHeader1">
	<div class="leftDiscussionBlogHeader">
		<span class="createNodeLink">
		   <a href="${url.context}/page/site/${page.url.templateArgs.site}/blog-postedit">
		      ${msg("header.createPost")}
		   </a>
		</span>
		<span class="rssLink">
		   <a href="${url.context}/service/components/blog/rss?site=${page.url.templateArgs.site}">
	          ${msg("header.blogRSS")}
	       </a>
		</span>
		
		<span class="configLink">
		   <a id="${args.htmlid}-config-blog-button" href="#">
	          ${msg("header.configureBlog")}
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
		<@blogpostlistLib.postListTitle filter=filter fromDate=fromDate toDate=toDate tag=tag />
	</div>
	
	<div class="rightDiscussionBlogHeader">
		<@paginatorLib.renderPaginatorModule htmlid=args.htmlid pdata=pdata />
	</div>
	
</div>

<div id="${args.htmlid}-postlist">
   <@blogpostlistLib.postListHTML htmlid=args.htmlid posts=items viewmode=viewmode />
</div>