<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   var userProfile = new Alfresco.cloud.component.ChangePassword("${el}").setOptions(
   {
      <#if passwordPolicy??>minPasswordLength: ${passwordPolicy.minLength},
      minPasswordUpper: ${passwordPolicy.minCharsUpper},
      minPasswordLower: ${passwordPolicy.minCharsLower},
      minPasswordNumeric: ${passwordPolicy.minCharsNumeric},
      minPasswordSymbols: ${passwordPolicy.minCharsSymbols}</#if>
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="password profile">
   <form id="${el}-form" action="${url.context}/service/components/profile/change-password" method="post">
      <div class="header-bar">${msg("label.changepassword")}</div>
      <div class="row">
         <span class="label"><label for="${el}-oldpassword">${msg("label.oldpassword")}:</label></span>
         <span><input type="password" maxlength="255" size="30" id="${el}-oldpassword" /></span>
      </div>
      <div class="row">
         <span class="label passwordWithStrengthMeter"><label for="${el}-newpassword1">${msg("label.newpassword")}:</label></span>
         <span>
            <input type="password" maxlength="255" size="30" id="${el}-newpassword1" />
         </span>
         <div id="${el}-passwordStrengthMeter"></div>
      </div>
      <div class="row">
         <span class="label passwordWithHelpText"><label for="${el}-newpassword2">${msg("label.confirmpassword")}:</label></span>
         <span>
            <input type="password" maxlength="255" size="30" id="${el}-newpassword2" />
            <p class="tiny">${msg(passwordHelpLabel!"")}</p>
         </span>
      </div>
      
      <hr/>
      
      <div class="buttons">
         <button id="${el}-button-ok" name="save">${msg("button.ok")}</button>
         <button id="${el}-button-cancel" name="cancel">${msg("button.cancel")}</button>
      </div>
   
   </form>
</div>