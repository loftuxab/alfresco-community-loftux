<#import "global/alfresco-template.ftl" as template />
<@template.header>
</@>
<@template.body>
   <div id="doc3">
   <div id="hd">
         <@region id="header" scope="global" protected=true />
         <@region id="title" scope="page" protected=true />
   </div>
   <@region id="calendar" scope="page" protected=true />
   <@region id="view" scope="page" protected=true />
   </div>
</@>