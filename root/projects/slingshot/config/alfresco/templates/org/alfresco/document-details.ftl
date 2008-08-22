<#import "import/alfresco-template.ftl" as template />
<@template.header>
   <link rel="stylesheet" type="text/css" href="${url.context}/components/blog/postlist.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/components/blog/postview.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/templates/document-details/document-details.css" />
   
   <!-- General Blog Assets (used for commenting) -->
   <script type="text/javascript" src="${page.url.context}/components/blog/blogdiscussions-common.js"></script>
   <script type="text/javascript" src="${page.url.context}/components/blog/blog-common.js"></script>
   <script type="text/javascript" src="${page.url.context}/templates/document-details/document-details.js"></script>
   
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="path" scope="template" protected=true />
      
      <div class="yui-g">
         <div class="yui-g first">
            <@region id="preview" scope="template" protected=true />
            <div class="document-details-comments">
               <@region id="comments" scope="template" protected=true />
               <@region id="createcomment" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-g"> 
            <div class="yui-u first"> 
               <@region id="document-info" scope="template" protected=true />
            </div>
            <div class="yui-u">
               <@region id="document-actions" scope="template" protected=true />
               <@region id="document-links" scope="template" protected=true />
            </div>
         </div>
      </div>
   </div>
   
   <script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentDetails().setOptions(
   {
      nodeRef: "${url.args.nodeRef}"
   });
   //]]></script>

</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>