<#import "import/alfresco-template.ftl" as template />
<#import "import/alfresco-layout.ftl" as layout />
<@template.header>
   <link rel="stylesheet" type="text/css" href="${url.context}/templates/dashboard/customise-dashboard.css" />
</@>

<@template.body>
<div id="hd">
   <@region id="header" scope="global" protected=true />
   <@region id="title" scope="page" protected=true />
   <h1 class="sub-title">${page.title}</h1>
</div>
<div id="bd">
   <@region id="customise-layout" scope="page" protected=true />
   <@region id="customise-dashlets" scope="page" protected=true />
</div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>