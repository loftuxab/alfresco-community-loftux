<#include "include/alfresco-template.ftl" />
<@templateHeader>
</@>

<@templateBody>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   
   <div id="bd">

	   <@region id="membersbar" scope="template" protected=true />
   
      <div>
         <@region id="sent-invites" scope="template" />
      </div>
	</div>
	
    <br />
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>