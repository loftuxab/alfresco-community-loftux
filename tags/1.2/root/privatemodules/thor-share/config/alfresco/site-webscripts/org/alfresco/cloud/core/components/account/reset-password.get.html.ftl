<#assign el=args.htmlid?html/>
<#function paramMsg key>
   <#return msg(key, user.id!"", url.context, url.context + (postLogoutRedirectPage!""))/>
</#function>
<div id="${el}-body" class="cloud-reset-password theme-overlay hidden">
   <!-- Logo -->
   <div class="theme-company-logo"></div>

   <#if error??>
      <div id="${el}-error-container" class="reset-password-error theme-border-3 theme-bg-color-8">
         <h3 class="thin error">${paramMsg("error.header." + error)}</h3>
         <hr/>
         <#if paramMsg("error.text." + error) != ("error.text." + error)>
         <p>${paramMsg("error.text." + error)?html}</p>
         </#if>
         <#if paramMsg("error.link." + error) != "error.link." + error>
         <p>${paramMsg("error.link." + error)}</p>
         </#if>
      </div>
   <#else>
      <div id="${el}-confirmation-container" class="reset-password-confirmation theme-border-3 theme-bg-color-8 hidden">
         <h2 class="theme-color-1 thin">${paramMsg("header.reset-password-success")}</h2>
         <p>${paramMsg("text.reset-password-success")}</p>
      </div>

      <div id="${el}-form-container" class="reset-password-form theme-border-3 theme-bg-color-8">
         <h2 class="theme-color-1 thin">${paramMsg("header.reset-password")}</h2>
         <p>${paramMsg("text.reset-password")}</p>

         <form id="${el}-form" method="POST" action="${url.context}/proxy/alfresco-noauth/internal/cloud/users/passwords" enctype="application/json" class="form-fields">
            <input type="hidden" name="id" value="${args.pid?js_string}"/>
            <input type="hidden" name="key" value="${args.key?js_string}"/>

            <!-- Password -->
            <div class="yui-gd form-field">
               <div class="yui-u first">
                  <label for="${el}-password">${paramMsg("label.newPassword")}:</label>
               </div>
               <div class="yui-u">
                  <input id="${el}-password" type="password" name="password" tabindex="0"/> *
                  <div id="${el}-passwordStrengthMeter"></div>
               </div>
               <div class="yui-u first">
                  <label for="${el}-password2">${paramMsg("label.confirmPassword")}:</label>
               </div>
               <div class="yui-u">
                  <input id="${el}-password2" type="password" name="-" tabindex="0"/> *
               </div>
            </div>

            <div class="yui-gd buttons">
               <div class="yui-u first">&nbsp;</div>
               <div class="yui-u">
                  <button id="${el}-submit">${paramMsg("button.savePassword")}</button>
               </div>
            </div>
         </form>
      </#if>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
new Alfresco.cloud.component.ResetPassword("${args.htmlid?js_string}").setOptions({
   username: <#if username??>"${username?js_string}"<#else>null</#if>,
   <#if passwordPolicy??>minPasswordLength: ${passwordPolicy.minLength},
   minPasswordUpper: ${passwordPolicy.minCharsUpper},
   minPasswordLower: ${passwordPolicy.minCharsLower},
   minPasswordNumeric: ${passwordPolicy.minCharsNumeric},
   minPasswordSymbols: ${passwordPolicy.minCharsSymbols},</#if>
   // Additional message params
   currentUsername: "${(user.id!"")?js_string}",
   postLogoutRedirectUrl: <#if redirectPage??>"${url.context}${postLogoutRedirectPage}"<#else>null</#if>
}).setMessages(${messages});
//]]></script>
