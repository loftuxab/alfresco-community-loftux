<#import "import/alfresco-template.ftl" as template />
<@template.header>
   <script type="text/javascript" src="${url.context}/templates/blog/blog-postview.css"></script>
   <script type="text/javascript" src="${url.context}/templates/blog/blog-postview.js"></script>
   
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
       <@region id="postview" scope="template" protected=true />
       <@region id="comments" scope="template" protected=true />
       <@region id="createcomment" scope="template" protected=true />
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>