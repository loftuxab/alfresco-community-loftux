<#import "import/alfresco-template.ftl" as template />
<@template.header>
<script type="text/javascript">//<![CDATA[
YAHOO.util.Event.onContentReady("bd", function()
{
   var Dom = YAHOO.util.Dom;
   
   // Find the People Finder by name
   var peopleFinder = Alfresco.util.ComponentManager.findFirst("Alfresco.PeopleFinder");
   
   // Set the correct options for our use
   peopleFinder.setOptions(
   {
      userProfileMode: true
   });

   YAHOO.Bubbling.on("personSelected", onPersonSelected, this);
   
   function onPersonSelected(layer, args)
   {
      var obj = args[1];
      if (obj && obj.userName)
      {
         document.location.href = Alfresco.util.uriTemplate("userprofilepage",
         {
            userid: obj.userName
         });
      }
   }
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