<script type="text/javascript">//<![CDATA[
   var userProfile = new Alfresco.ChangePassword("${args.htmlid}").setOptions(
   {
      minPasswordLength: "${config.scoped['Users']['users'].getChildValue('password-min-length')}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${args.htmlid}-body" class="password">
   <form id="${htmlid}-form" action="${url.context}/service/components/profile/change-password" method="post">
   
      <div class="header-bar">${msg("label.changepassword")}</div>
      <div class="row">
         <span class="label"><label for="${args.htmlid}-oldpassword">${msg("label.oldpassword")}:</label></span>
         <span class="input"><input type="password" maxlength="255" size="30" id="${args.htmlid}-oldpassword" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${args.htmlid}-newpassword1">${msg("label.newpassword")}:</label></span>
         <span class="input"><input type="password" maxlength="255" size="30" id="${args.htmlid}-newpassword1" /></span>
      </div>
      <div class="row">
         <span class="label"><label for="${args.htmlid}-newpassword2">${msg("label.confirmpassword")}:</label></span>
         <span class="input"><input type="password" maxlength="255" size="30" id="${args.htmlid}-newpassword2" /></span>
      </div>
      
      <hr/>
      
      <div class="buttons">
         <button id="${args.htmlid}-button-ok" name="save">${msg("button.ok")}</button>
         <button id="${args.htmlid}-button-cancel" name="cancel">${msg("button.cancel")}</button>
      </div>
   
   </form>

</div>