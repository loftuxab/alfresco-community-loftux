<#assign el=args.htmlid?html/>
<div class="share-form">
   <div class="form-manager">
      <h1>${msg("header")}</h1>
   </div>
   <div id="${el}-container" class="form-container">
      <form id="${el}-form" method="POST" action="${url.context}/proxy/alfresco/internal/cloud/accounts/signupqueue" enctype="application/json">
         <div id="${el}-fields" class="form-fields">
            <div class="form-field">
               <label for="${el}-email">${msg("email.label")}:</label>
               <input id="${el}-email" type="text" name="email" tabindex="0" class="mandatory" placeholder="${msg("email.placeholder")}"/>
               <input id="${el}-source" type="hidden" name="source" value="test-share-signup-page"/>
            </div>
         </div>
         <div class="form-buttons">
            <button id="${el}-submit">${msg("signup.button")}</button>
         </div>
      </form>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
   new Alfresco.cloud.component.Signup("${args.htmlid?js_string}").setOptions({}).setMessages(${messages});
//]]></script>
