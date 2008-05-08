<#import "global/alfresco-template.ftl" as template />
<@template.header>
</@>
<@template.body>
<div id="doc3">
   <div id="hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="site-content">
         <@region id="doclib" scope="page"/>
      </div>
   </div>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
   <div class="hiddenComponents">
      <@region id="fileupload" scope="page"/>
   </div>
</div>
</@>