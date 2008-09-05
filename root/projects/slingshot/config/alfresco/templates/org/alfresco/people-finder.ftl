<#import "import/alfresco-template.ftl" as template />
<@template.header>
<script type="text/javascript">//<![CDATA[
YAHOO.util.Event.onContentReady("bd", function()
{
   // Find the People Finder by name
   var peopleFinder = Alfresco.util.ComponentManager.findFirst("Alfresco.PeopleFinder");
   
   // Set the correct options for our use
   peopleFinder.setOptions(
   {
      peopleListMode: true
   });
});
//]]></script>
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="people-finder" scope="template" />
   </div>
</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>