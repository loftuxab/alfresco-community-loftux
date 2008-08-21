<script type="text/javascript">//<![CDATA[
   var userProfile = new Alfresco.ChangePassword("${args.htmlid}").setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="password">
   <form id="${htmlid}-form" action="${url.serviceContext}/components/profile/change-password" method="post">
   
      <div class="header-bar">${msg("label.changepassword")}</div>
      <div class="title">${msg("label.oldinfo")}</div>
      <div class="row">
         <span class="label">${msg("label.oldpassword")}:</span>
         <span class="input"><input type="password" maxlength="256" size="30" id="${args.htmlid}-oldpassword" /></span>
      </div>
      <div class="title">${msg("label.newinfo")}</div>
      <div class="row">
         <span class="label">${msg("label.newpassword")}:</span>
         <span class="input"><input type="password" maxlength="256" size="30" id="${args.htmlid}-newpassword1" /></span>
      </div>
      <div class="row">
         <span class="label">${msg("label.confirmpassword")}:</span>
         <span class="input"><input type="password" maxlength="256" size="30" id="${args.htmlid}-newpassword2" /></span>
      </div>
      
      <hr/>
      
      <div class="buttons">
         <button id="${args.htmlid}-button-ok" name="save">${msg("button.ok")}</button>
         <button id="${args.htmlid}-button-cancel" name="cancel">${msg("button.cancel")}</button>
      </div>
   
   </form>

</div>