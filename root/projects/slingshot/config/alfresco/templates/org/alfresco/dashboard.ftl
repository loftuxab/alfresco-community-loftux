<#include "include/alfresco-template.ftl" />
<#import "import/alfresco-layout.ftl" as layout />
<@templateHeader "transitional">
   <#-- allow theme to be specified in url args - helps debugging themes -->
   <#assign theme = (page.url.args.theme)!theme />
   <@link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/dashboard.css" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/dashboard-presentation.css" />
   <@script type="text/javascript" src="${url.context}/js/dashlet-resizer.js"></@script>
</@>

<@templateBody>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="page" protected=true />
      <@region id="navigation" scope="page" protected=true />
   </div>
   <div id="bd">
      <@layout.grid gridColumns gridClass "component" />
   </div>
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>