<#import "global/alfresco-template.ftl" as template />
<#import "global/alfresco-layout.ftl" as layout />
<@template.header>
   <link rel="stylesheet" type="text/css" href="${url.context}/templates/dashboard/dashboard.css" />
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@layout.grid 3 "fluid" 9 "component-"/>
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>