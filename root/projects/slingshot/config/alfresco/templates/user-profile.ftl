<#import "global/alfresco-template.ftl" as template />
<@template.header />

<@template.body>
   <body class="yui-skin-sam">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
      <div class="site-content">
      <@region id="user-profile-dashlet" scope="page" />
      </div>
      <@region id="footer" scope="global" protected=true />
   </body>
</@>