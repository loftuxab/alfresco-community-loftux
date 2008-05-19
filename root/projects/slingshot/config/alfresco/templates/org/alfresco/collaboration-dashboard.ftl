<#import "global/alfresco-template.ftl" as template />
<#import "global/alfresco-layout.ftl" as layout />
<@template.header/>
<@template.body>
   <@region id="header" scope="global" protected=true/>
   <@region id="title" scope="template" protected=true />
   <@region id="navigation" scope="template" protected=true />
   <@layout.grid 3 "fluid" 9 "component-"/>
   <@region id="footer" scope="global" protected=true />
</@>