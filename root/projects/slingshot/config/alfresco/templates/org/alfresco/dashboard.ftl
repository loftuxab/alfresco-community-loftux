<#include "include/alfresco-template.ftl" />
<#import "import/alfresco-layout.ftl" as layout />
<@templateHeader "transitional">
   <@link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/dashboard.css" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/dashboard-presentation.css" />   
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