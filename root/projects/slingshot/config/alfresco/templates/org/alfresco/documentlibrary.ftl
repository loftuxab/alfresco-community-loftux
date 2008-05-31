<#import "import/alfresco-template.ftl" as template />
<@template.header>
   <link rel="stylesheet" type="text/css" href="${url.context}/templates/documentlibrary/documentlibrary.css" />
   <script type="text/javascript" src="${url.context}/templates/documentlibrary/documentlibrary.js"></script>
   <script type="text/javascript" src="${url.context}/modules/documentlibrary/doclib-actions.js"></script>
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="page" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="yui-t1" id="divDocLibraryWrapper">
         <div id="yui-main">
            <div class="yui-b" id="divDocLibraryDocs">
               <@region id="documentlist" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-b" id="divDocLibraryFilters">
            <@region id="filter" scope="template" protected=true />
            <@region id="tree" scope="template" protected=true />
         </div>
      </div>
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>