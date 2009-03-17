<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/invite/invite.css" />
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   
   <div id="bd">

	   <@region id="membersbar" scope="template" protected=true />
   
		<!-- start component code -->
		<div class="yui-g grid">

			<div class="yui-u first column1">
					<div class="yui-b" id="byAlfrescoUsers">
					   <@region id="people-finder" scope="template" protected=true />
					</div>

					<div class="yui-b" id="byEmail">
					   <@region id="addemail" scope="template" protected=true />
					</div>
			</div>

			<div class="yui-u column2">
					<div class="yui-b" id="invitationList">
						<@region id="invitationlist" scope="template" protected=true />
					</div>
			</div>

		</div>
		<!-- end component code -->
	</div>
	
    <br />
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>