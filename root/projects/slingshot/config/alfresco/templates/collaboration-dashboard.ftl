<#import "global/alfresco-template.ftl" as template />
<@template.header>
   <#include "util/3-column-head.ftl"/>
</@>

<@template.body>
   <body class="yui-skin-sam">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
      <div class="site-content">
      <#include "util/3-column-html.ftl"/>
      </div>
      <@region id="footer" scope="global" protected=true />
   </body>
</@>