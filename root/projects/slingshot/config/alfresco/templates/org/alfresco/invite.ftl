<#import "import/alfresco-template.ftl" as template />
<@template.header>
  <link rel="stylesheet" type="text/css" href="${url.context}/templates/invite/invite.css" />
</@>

<@template.body>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <div class="yui-t1" id="divInviteWrapper">
         <div id="yui-main">
            <div class="yui-b" id="divInviationList">
               <@region id="invitationlist" scope="template" protected=true />
            </div>
         </div>
         <div class="yui-b" id="divInviteCompoments">
            <@region id="inviteusers" scope="template" protected=true />
            <br />
            <br />
            <@region id="addemail" scope="template" protected=true />
         </div>
      </div>
   </div>

</@>

<@template.footer>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>