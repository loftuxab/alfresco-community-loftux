<#import "import/alfresco-template.ftl" as template />
<@template.header />

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
	<div class="yui-gf">
		<div class="yui-u first">
   			<@region id="calendar" scope="template" protected=true />
   		</div>
		<div class="yui-u">
			<@region id="view" scope="template" protected=true />
   		</div>
 	</div>
	</div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>