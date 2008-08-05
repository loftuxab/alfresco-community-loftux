<#import "/org/alfresco/modules/discussions/topiclist.lib.ftl" as topiclistLib/>
<#import "/org/alfresco/modules/paginator/paginator.lib.ftl" as paginatorLib/>

<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsTopicList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      startIndex: "${pdata.startIndex}",
      pageSize: "${pdata.pageSize}",
      filter: "${filter?html?j_string}",
      tag: "${tag?html?j_string}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="topiclist-header">
   <div id="${args.htmlid}-topiclistBar" class="yui-g toolbar">
      <div class="yui-u first">
         <div class="createTopic hideable"><button id="${args.htmlid}-createTopic-button" name="postlist-createTopic-button">${msg("header.createTopic")}</button></div>
         <div class="separator hideable">|</div>
         <div id="${args.htmlid}-rssFeed" class="rss-feed hideable">
            <a href="${url.context}/service/components/discussions/rss?site=${page.url.templateArgs.site}">
	           ${msg("header.discussionsRSS")}
            </a>
         </div>
      </div>
      <div class="yui-u align-right">
         <button id="${args.htmlid}-simpleView-button" name="topiclist-simpleView-button">${msg("header.simpleList")}</button>
      </div>
   </div>

   <div class="topiclist-infobar">
      <div id="${args.htmlid}-listtitle" class="leftDiscussionBlogHeader listTitle">
         <@topiclistLib.topicListTitle filter=filter tag=tag />
      </div>
      <div class="align-right paginator">
         <@paginatorLib.renderPaginatorModule htmlid=args.htmlid pdata=pdata />
      </div>
   </div>
</div>

<div id="${args.htmlid}-topiclist">
<@topiclistLib.topicListHTML htmlid=args.htmlid topics=items viewmode=viewmode />
</div>