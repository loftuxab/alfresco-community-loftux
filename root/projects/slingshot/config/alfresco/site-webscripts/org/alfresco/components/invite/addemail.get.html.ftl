
<script type="text/javascript">//<![CDATA[
   new Alfresco.AddEmailInvite("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="inviteusers">

   First name: <input type="text" id="${args.htmlid}-firstname" /><br />
   Last name: <input type="text" id="${args.htmlid}-lastname" /><br />
   Email: <input type="text" id="${args.htmlid}-email" /><br />
   <input type="button" value="Add user" id="${args.htmlid}-add-email-button" />
   
</div>