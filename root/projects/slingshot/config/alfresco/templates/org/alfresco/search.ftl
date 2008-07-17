<#import "import/alfresco-template.ftl" as template />
<@template.header>
  <link rel="stylesheet" type="text/css" href="${url.context}/templates/wiki/wiki.css" />
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="yui-t1">
         <div id="yui-main">
            <div class="yui-b">
               <@region id="search" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-b">
         </div>
      </div>
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>