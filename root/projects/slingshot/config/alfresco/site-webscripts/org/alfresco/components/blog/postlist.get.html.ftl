
<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostList("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!''}",
      containerId: "${args.container!'blog'}",
      initialFilter:
      {
         filterId: "${page.url.args.filterId!'new'}",
         filterOwner: "${page.url.args.filterOwner!'Alfresco.BlogPostListFilter'}",
         filterData: <#if page.url.args.filterData??>"${page.url.args.filterData}"<#else>null</#if>
      }
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="postlist-header">
   <div id="${args.htmlid}-postlistBar" class="yui-g toolbar">
      <div class="yui-u first">
         <div class="createPost hideable"><button id="${args.htmlid}-createPost-button" name="postlist-createPost-button">${msg("header.createPost")}</button></div>
         <div class="separator hideable">|</div>
         <div class="configureBlog hideable"><button id="${args.htmlid}-configureBlog-button" class="configureBlog" name="postlist-configureBlog-button">${msg("header.configureBlog")}</button></div>
         <div class="separator hideable">|</div>
         <div id="${args.htmlid}-rssFeed" class="rss-feed hideable"></div>
      </div>
      <div class="yui-u align-right">
         <button id="${args.htmlid}-simpleView-button" name="postlist-simpleView-button">${msg("header.simpleList")}</button>
      </div>
   </div>


   <div class="postlist-infobar yui-gd">
      <div class="yui-u first">
         <div id="${args.htmlid}-listtitle" class="listTitle">
            ${msg("title.postlist")}
         </div>
      </div>
      <div class="yui-u align-right">
         <div id="${args.htmlid}-paginator" class="paginator"></div>
      </div>
   </div>
</div>

<div id="${args.htmlid}-postlist" class="blog-postlist">
</div>
