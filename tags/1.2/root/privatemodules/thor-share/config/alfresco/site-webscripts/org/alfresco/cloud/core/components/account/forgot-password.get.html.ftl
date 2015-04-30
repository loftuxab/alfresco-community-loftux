<#assign el=args.htmlid?html/>
<div id="${el}-body" class="cloud-forgot-password theme-overlay hidden">
   <!-- Logo -->
   <div class="theme-company-logo"></div>

   <div id="${el}-confirmation-container" class="forgot-password-confirmation theme-border-3 theme-bg-color-8 hidden">
      <h2 class="theme-color-1 thin">${msg("header.forgot-password-success")}</h2>
      <p></p>
   </div>

   <div id="${el}-form-container" class="forgot-password-form theme-border-3 theme-bg-color-8">
      <h2 class="theme-color-1 thin">${msg("header.forgot-password")}</h2>

      <p>${msg("text.forgot-password")}</p>

      <form id="${el}-form" method="POST" action="${url.context}/proxy/alfresco-noauth/internal/cloud/users/passwords/resetrequests" enctype="application/json" class="form-fields">

         <!-- Email -->
         <div class="yui-gd form-field">
            <div class="yui-u first">
               <label for="${el}-username">${msg("label.email")}:</label>
            </div>
            <div class="yui-u">
               <input id="${el}-username" type="text" name="username" tabindex="0"/> *
            </div>
         </div>

         <div class="yui-gd buttons">
            <div class="yui-u first">&nbsp;</div>
            <div class="yui-u">
               <button id="${el}-submit">${msg("button.send-instructions")}</button>
               <button id="${el}-cancel">${msg("button.cancel")}</button>
            </div>
         </div>
      </form>

   </div>
</div>

<script type="text/javascript">//<![CDATA[
new Alfresco.cloud.component.ForgotPassword("${args.htmlid?js_string}").setOptions({}).setMessages(${messages});
//]]></script>
