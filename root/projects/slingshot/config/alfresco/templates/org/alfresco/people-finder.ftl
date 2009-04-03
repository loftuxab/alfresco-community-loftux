<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <script type="text/javascript">//<![CDATA[
   YAHOO.util.Event.onContentReady("bd", function()
   {
      // Find the People Finder by name
      var peopleFinder = Alfresco.util.ComponentManager.findFirst("Alfresco.PeopleFinder");
   
      // Set the correct options for our use
      peopleFinder.setOptions(
      {
         viewMode: Alfresco.PeopleFinder.VIEW_MODE_FULLPAGE
      });
   });
   //]]></script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="people-finder" scope="template" />
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>