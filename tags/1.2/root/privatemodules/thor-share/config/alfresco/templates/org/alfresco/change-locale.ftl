<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region id="share-header" scope="global" chromeless="true"/>
      <@region id="title" scope="template" />
   </div>
   <div id="bd">
      <@region id="toolbar" scope="template" />
      <@region id="change-locale" scope="template" />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>