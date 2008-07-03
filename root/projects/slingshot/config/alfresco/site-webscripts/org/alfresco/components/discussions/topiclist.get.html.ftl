<#import "/org/alfresco/modules/discussions/topiclist.lib.ftl" as topiclistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>

<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsTopicList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="discussionsBlogHeader1">
	<div class="leftDiscussionBlogHeader">
		<span class="createNodeLink">
		   <a href="${url.context}/page/site/${page.url.templateArgs.site}/discussions-createtopic">
		      ${msg("header.createTopic")}
		   </a>
		</span>
		<span class="rssLink">
		   <a href="${url.context}/service/components/discussions/rss?site=${page.url.templateArgs.site}">
	          ${msg("header.discussionsRSS")}
	       </a>
		</span>
		
		<span class="rssLink">
		   <a href="${url.context}/service/components/discussions/rss/latestposts?site=${page.url.templateArgs.site}">
	          ${msg("header.topicsAndRepliesRSS")}
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

<div id="${args.htmlid}-topiclist">
<@topiclistLib.topicListHTML topics=items viewmode=viewmode />
</div>