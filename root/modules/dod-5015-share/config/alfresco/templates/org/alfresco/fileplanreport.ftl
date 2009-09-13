<#include "include/alfresco-template.ftl" />
<#import "import/alfresco-layout.ftl" as layout />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/disposition-edit/disposition-edit.css" />
</@>

<@templateBody>
<div id="bd">
   <@region id="fileplanreport" scope="template" protected=true />
   <script type="text/javascript">//<![CDATA[
       window.print();   
   //]]></script>
</div>
</@>
