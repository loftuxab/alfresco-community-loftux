<#include "include/alfresco-template.ftl" />
<#import "import/alfresco-layout.ftl" as layout />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/site/customise-site.css" />
</@>

<@templateBody>
<div id="hd">
   <@region id="header" scope="global" protected=true />
   <@region id="title" scope="template" protected=true />
   <h1 class="sub-title">${page.title}</h1>
</div>
<div id="bd">
   <@region id="customise-pages" scope="template" protected=true />
</div>
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>