<#import "import/alfresco-template.ftl" as template />
<@template.header>
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
   </div>
   <div id="bd">
      <h2>Form Test Page</h2>
      <@region id="form-ui" scope="template" />
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>