<#import "global/alfresco-template.ftl" as template />
<@template.header />

<@template.body>
   <body class="yui-skin-sam">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
      <div class="site-content">
      <@region id="details" scope="page">
        TODO: Bind details components
      </@region>
      </div>
      <@region id="footer" scope="global" protected=true />
      </div>
   </body>
</@>