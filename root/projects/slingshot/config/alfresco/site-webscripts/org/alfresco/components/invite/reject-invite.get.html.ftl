<#-- title -->
<div class="page-title">
   <div class="title">
      <h1>${msg("header.title")}</h1>
   </div>
</div>

<#-- error -->
<#if (error?? && error)>
<div class="reject-invite-body">
   <h1>Invitation not found</h1>
   <p>No invitation found. The inviter has probably canceled the invitation in the meantime.</p>
</div>

<#-- content -->
<#else>
<script type="text/javascript">//<![CDATA[
   new Alfresco.DeclineInvite("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args.siteShortName!''}",
      inviteId: "jbpm$${page.url.args.inviteId!''}",
      inviteeUserName: "${page.url.args.inviteeUserName!''}",
      inviteTicket: "${page.url.args.inviteTicket!''}",
   }).setMessages(
      ${messages}
   );
//]]></script>

<div class="reject-invite-body">
   <div id="${args.htmlid}-confirm" class="main-content">
      <div class="question">
         Are you sure you want to reject the invitation from ||USER|| to join the<br />
         <span class="site-name"><#if (profile.title != "")>${profile.title}<#else>${profile.shortName}</#if></span> collaboration site?
      </div>
      <div class="actions">
         <span id="${args.htmlid}-decline-button" class="yui-button yui-push-button"> 
            <span class="first-child"> 
               <input type="button" name="decline-button" value="Yes, Reject"> 
            </span> 
         </span> 
         <span id="${args.htmlid}-accept-button" class="yui-button yui-push-button"> 
            <span class="first-child"> 
               <input type="button" name="accept-button" value="No, Accept">
            </span>
         </span>
      </div>
   </div>
   
   <div id="${args.htmlid}-declined" class="main-content hidden">
      <p>||User||'s invitation to join the
      <#if (profile.title != "")>${profile.title}<#else>${profile.shortName}</#if> site
      has been rejected.<p>
   </div>
   
   <div id="${args.htmlid}-learn-more" class="learn-more">
      <p>Learn more about Share at <a href="http://www.alfresco.com">www.alfresco.com</a></p>
   </div>
</div>
</#if>