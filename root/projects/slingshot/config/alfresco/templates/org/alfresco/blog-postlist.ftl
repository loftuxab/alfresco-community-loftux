<#import "import/alfresco-template.ftl" as template />
<@template.header>
   <link rel="stylesheet" type="text/css" href="${url.context}/templates/blog/blog-postlist.css" />
   <script type="text/javascript" src="${url.context}/templates/blog/blog-postlist.js"></script>
   
   <!-- General Blog Assets -->
   <script type="text/javascript" src="${page.url.context}/components/blog/blogdiscussions-common.js"></script>
   <script type="text/javascript" src="${page.url.context}/components/blog/blog-common.js"></script>
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="yui-t1" id="divPostListWrapper">
         <div id="yui-main">
            <div class="yui-b" id="divPostListPosts">
               <@region id="postlist" scope="template" protected=true />
            </div>
         </div>
	         <div class="yui-b" id="divPostListFilters">
	            <@region id="filters" scope="template" protected=true />
	   			<@region id="archives" scope="template" protected=true />
	            <@region id="tags" scope="template" protected=true />
	         </div>
      </div>
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>