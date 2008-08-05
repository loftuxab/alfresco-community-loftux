<#import "/org/alfresco/modules/blog/blogpostlist.lib.ftl" as blogpostlistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>

<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      startIndex: "${pdata.startIndex}",
      pageSize: "${pdata.pageSize}",
      filter: "${filter?html?j_string}",
      tag: "${tag?html?j_string}"
      <#if fromDateInt??>,
      ,fromDate: "${fromDateInt?string}"
      </#if>
   }).setMessages(
      ${messages}
   );
//]]></script>

<script type="text/javascript">//<![CDATA[
   new Alfresco.ConfigBlog("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "blog"
   }).setMessages(
      ${messages}
   );;
//]]></script>
<div class="postlist-header">
   <div id="${args.htmlid}-postlistBar" class="yui-g toolbar">
      <div class="yui-u first">
         <div class="createPost hideable"><button id="${args.htmlid}-createPost-button" name="postlist-createPost-button">${msg("header.createPost")}</button></div>
         <div class="separator hideable">|</div>
         <div class="configureBlog hideable"><button id="${args.htmlid}-configureBlog-button" class="configureBlog" name="postlist-configureBlog-button">${msg("header.configureBlog")}</button></div>
         <div class="separator hideable">|</div>
         <div id="${args.htmlid}-rssFeed" class="rss-feed hideable">
            <a href="${url.context}/service/components/blog/rss?site=${page.url.templateArgs.site}">
	           ${msg("header.blogRSS")}
            </a>
         </div>
      </div>
      <div class="yui-u align-right">
         <button id="${args.htmlid}-simpleView-button" name="postlist-simpleView-button">${msg("header.simpleList")}</button>
      </div>
   </div>

   <div class="postlist-infobar">
      <div id="${args.htmlid}-listtitle" class="leftDiscussionBlogHeader listTitle">
         <@blogpostlistLib.postListTitle filter=filter fromDate=fromDate toDate=toDate tag=tag />
      </div>
      <div class="align-right">
         <@paginatorLib.renderPaginatorModule htmlid=args.htmlid pdata=pdata />
      </div>
   </div>
</div>
<div id="${args.htmlid}-postlist" class="blog-postlist">
   <@blogpostlistLib.postListHTML htmlid=args.htmlid posts=items viewmode=viewmode />
</div>