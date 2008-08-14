<#import "import/alfresco-template.ftl" as template />
<@template.header>
  <link rel="stylesheet" type="text/css" href="${url.context}/templates/wiki/wiki.css" />
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
	   <@region id="toolbar" scope="template" protected=true />
		<@region id="wikipage" scope="template" protected=true />
 	</div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>