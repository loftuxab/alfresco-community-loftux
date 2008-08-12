<#import "import/alfresco-template.ftl" as template />
<#import "import/alfresco-layout.ftl" as layout />
<@template.header "transitional">
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/dashboard.css" />
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="page" protected=true />
      <@region id="navigation" scope="page" protected=true />
   </div>
   <div id="bd">
      <@layout.grid gridColumns gridClass "component" />
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>