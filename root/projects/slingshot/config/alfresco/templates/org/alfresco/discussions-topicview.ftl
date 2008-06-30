<#import "import/alfresco-template.ftl" as template />
<@template.header>
   <link rel="stylesheet" type="text/css" href="${url.context}/components/discussions-blog-common.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/components/discussions/discussions.css" />
   <script type="text/javascript" src="${url.context}/templates/discussions/discussions-topicview.js"></script>
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="page" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
       <@region id="topic" scope="template" protected=true />
       <@region id="replies" scope="template" protected=true />
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>