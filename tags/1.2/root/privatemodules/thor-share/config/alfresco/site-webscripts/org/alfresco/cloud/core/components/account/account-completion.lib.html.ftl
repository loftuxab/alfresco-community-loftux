<#assign el=args.htmlid?html/>
<#assign mode=args.mode!""/>
<#function paramMsg key>
   <#return msg(key,
   user.id!"",
   inviterName!"",
   inviteSiteTitle!"",
   url.context,
   postLogoutRedirectPage!"",
   currentPage!"",
   config.scoped["Cloud"]["signup"].getChildValue("url"),
   config.scoped["Cloud"]["signup"].getChildValue("email"),
   config.scoped["Cloud"]["legal"].getChildValue("terms"),
   config.scoped["Cloud"]["legal"].getChildValue("privacy"))/>
</#function>

<div id="${el}-body" class="cloud-account-completion theme-overlay hidden">
   <!-- Logo -->
   <div class="theme-company-logo"></div>

   <#if notification??>

      <!-- Notification -->
      <div class="message theme-border-3 theme-bg-color-8">
         <h3 class="thin">${paramMsg("notification.header." + notification)}</h3>
         <hr/>
         <#if paramMsg("notification.text." + notification) != ("notification.text." + notification)>
         <p>${paramMsg("notification.text." + notification)?html}</p>
         </#if>
         <#if paramMsg("notification.link." + notification) != "notification.link." + notification>
         <p>${paramMsg("notification.link." + notification)}</p>
         </#if>
      </div>

   <#elseif error??>

      <!-- Error -->
      <div class="message theme-border-3 theme-bg-color-8">
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

      <div class="account-info">

         <#if welcome??>

         <!-- Welcome -->
         <div class="thank-you">
            <div class="read-flow">
               <div class="read-flow-text theme-border-3 theme-bg-color-8">
                  <h3 class="thin">${paramMsg("welcome.header." + welcome)}</h3>
                  <hr>
                  ${paramMsg("welcome.text." + welcome)?html}
               </div>
               <div class="read-flow-arrow">
                  <div class="theme-flow-arrow"></div>
               </div>
            </div>
         </div>

         </#if>
            
         <#if about??><#assign about = "." + about/><#else><#assign about = ""/></#if>

         <!-- About -->
         <div class="about">
            <h3 class="thin">${paramMsg("about.header" + about)}</h3>
            <ul>
               <#-- Poor man's freemarker while loop -->
               <#list 1..10 as id>
                  <#if paramMsg("about.text" + about + "." + id) == ("about.text" + about + "." + id)>
                     <#break/>
                  </#if>
                  <li>${paramMsg("about.text" + about + "." + id)}</li>
               </#list>
            </ul>
         </div>
      </div>

      <#if profileForm?? || loginWithActionForm??>

         <#if profileForm??>
            <div class="account-form theme-border-3 theme-bg-color-8">
               <h2 class="theme-color-1 thin">${paramMsg("profile.header")}</h2>
               <form id="${el}-profileForm" method="POST" action="${url.context}/proxy/alfresco-noauth/${profileForm.action}" enctype="application/json" class="form-fields">
                  <#list (profileForm.hidden!{})?keys as key>
                     <input type="hidden" name="${key}" value="${profileForm.hidden[key]?html}"/>
                  </#list>

                  <!-- Username / Email -->
                  <div class="yui-gd form-field">
                     <div class="yui-u first">
                        <label>${msg("label.email")}:</label>
                     </div>
                     <div class="yui-u">
                     ${(username!"")?html}
                     </div>
                  </div>

                  <!-- Firstname & Lastname -->
                  <div class="yui-gd form-field">
                     <div class="yui-u first">
                        <label for="${el}-firstName">${msg("label.firstName")}:</label>
                     </div>
                     <div class="yui-u">
                        <input id="${el}-firstName" type="text" name="firstName" tabindex="0"/> *
                     </div>
                  </div>
                  <div class="yui-gd form-field">
                     <div class="yui-u first">
                        <label for="${el}-lastName">${msg("label.lastName")}:</label>
                     </div>
                     <div class="yui-u">
                        <input id="${el}-lastName" type="text" name="lastName" tabindex="0"/> *
                     </div>
                  </div>

                  <!-- Passwords -->
                  <div class="yui-gd form-field">
                     <div class="yui-u first">
                        <label for="${el}-password">${msg("label.password")}:</label>
                     </div>
                     <div class="yui-u">
                        <input id="${el}-password" type="password" name="password" tabindex="0"/> *
                        <div id="${el}-passwordStrengthMeter"></div>
                     </div>
                     <div class="yui-u first">
                         <label for="${el}-password2">${msg("label.confirmPassword")}:</label>
                     </div>
                     <div class="yui-u">
                        <input id="${el}-password2" type="password" name="-" tabindex="0"/> *<br>
                        <span class="tiny">${profileForm.passwordHelp!""}</span>
                     </div>
                  </div>

                  <!-- Terms & Conditions -->
                  <div class="yui-gd form-field">
                     <div class="yui-u first">
                        &nbsp;
                     </div>
                     <div class="yui-u">
                        <input id="${el}-terms" type="checkbox" name="-" tabindex="0"/>
                        <label for="${el}-terms" class="tiny">${paramMsg("label.accept")}</label>
                     </div>
                  </div>

                  <!-- Submit -->
                  <div class="yui-gd form-field">
                     <div class="yui-u first"><br></div>
                     <div class="yui-u">
                        <button id="${el}-submit"><#if profileForm.button??>${msg(profileForm.button)}<#else>${msg("profile.button")}</#if></button>
                     </div>
                  </div>

                  <!-- Link -->
                  <#if paramMsg("profile.link") != "profile.link">
                  <div class="form-field cloud-account-completion-links">
                        ${paramMsg("profile.link")}
                  </div>
                  </#if>

               </form>
            </div>

         <#elseif loginWithActionForm??>

            <div class="account-form theme-border-3 theme-bg-color-8">
               <h2 class="theme-color-1 thin">${paramMsg("login-with-action.header")}</h2>
               <form id="${el}-loginWithActionForm" method="POST" action="${url.context}/proxy/alfresco-noauth/${loginWithActionForm.action}" enctype="application/json" class="form-fields">
                  <#list (loginWithActionForm.hidden!{})?keys as key>
                     <input type="hidden" name="${key}" value="${loginWithActionForm.hidden[key]?html}"/>
                  </#list>

                  <!-- Username / Email -->
                  <div class="yui-gd form-field">
                     <div class="yui-u first">
                        <label for="${el}-username">${msg("label.username")}:</label>
                     </div>
                     <div class="yui-u">
                        <#if (loginWithActionForm.usernameControl!"input") == "input">
                        <input id="${el}-username" type="text" name="${loginWithActionForm.usernameName!"username"}" tabindex="0" value="${(loginWithActionForm.username!"")?html}"/>
                        <#elseif loginWithActionForm.usernameControl?? && loginWithActionForm.usernameControl == "label">
                        ${(loginWithActionForm.username!"")?html}
                        <input id="${el}-username" type="hidden" name="${loginWithActionForm.usernameName!"username"}" value="${(loginWithActionForm.username!"")?html}"/>
                        </#if>
                     </div>
                  </div>

                  <!-- Password -->
                  <div class="yui-gd form-field">
                     <div class="yui-u first">
                        <label for="${el}-password">${msg("label.password")}:</label>
                     </div>
                     <div class="yui-u">
                        <input id="${el}-password" type="password" name="password" tabindex="0"/>
                     </div>
                  </div>

                  <div class="yui-gd form-field">
                     <div class="yui-u first"><br/></div>
                     <div class="yui-u">
                        <button id="${el}-submit">${msg("login-with-action.button")}</button>
                     </div>
                  </div>

               </form>
            </div>

         </#if>

         <!-- Hidden login form that will be submitted after the form above is successfully submitted -->
         <div class="hidden">
            <form id="${el}-login" accept-charset="UTF-8" method="POST" action="${url.context}/page/dologin">
               <input id="${el}-login-username" type="hidden" name="username" value="${username?html}"/>
               <input id="${el}-login-password" type="hidden" name="password" value=""/>
               <input type="hidden" name="success" value="${tenantContext}${startpage!""}"/>
               <input type="hidden" name="failure" value="${tenantContext}/page/type/login?error=true"/>
            </form>
         </div>

      <#elseif loginForm??>

         <div class="account-form theme-border-3 theme-bg-color-8">
            <h2 class="theme-color-1 thin">${paramMsg("login.header")}</h2>
            <form id="${el}-loginForm" accept-charset="UTF-8" method="POST" action="${url.context}/page/dologin" class="form-fields">
               <input type="hidden" name="username" value="${username?html}"/>
               <input type="hidden" name="success" value="${tenantContext}${startpage!""}"/>
               <input type="hidden" name="failure" value="${tenantContext}/page/type/login?error=true"/>

               <!-- Username / Email -->
               <div class="yui-gd form-field">
                  <div class="yui-u first">
                     <label>${msg("label.username")}:</label>
                  </div>
                  <div class="yui-u">
                  ${(username!"")?html}
                  </div>
               </div>

               <!-- Password -->
               <div class="yui-gd form-field">
                  <div class="yui-u first">
                     <label for="${el}-password">${msg("label.password")}:</label>
                  </div>
                  <div class="yui-u">
                     <input id="${el}-password" type="password" name="password" tabindex="0"/>
                  </div>
               </div>

               <div class="yui-gd form-field">
                  <div class="yui-u first"><br/></div>
                  <div class="yui-u">
                     <button id="${el}-submit">${msg("login.button")}</button>
                     <a href="${applicationContext}/page/forgot-password" class="theme-color-1">${msg("label.forgotPassword")}</a>
                  </div>
               </div>
            </form>
         </div>
      </#if>

      <!-- iOS Notification -->
      <#if paramMsg("app.ios.header") != ("app.ios.header")>
         <div class="message ios-notification theme-border-3 theme-bg-color-8 hidden">
            <h3 class="thin">${paramMsg("app.ios.header")}</h3>
            <hr/>
            <#if paramMsg("app.ios.launch.text") != ("app.ios.launch.text")>
            <p>${paramMsg("app.ios.launch.text")?html}</p>
            </#if>
            <#if paramMsg("app.ios.launch.label") != "app.ios.launch.label">
            <p><button id="${el}-app-ios-launch">${paramMsg("app.ios.launch.label")}</button></p>
            </#if>
            <br/>
            <#if paramMsg("app.ios.install.text") != ("app.ios.install.text")>
            <p>${paramMsg("app.ios.install.text")?html}</p>
            </#if>
            <#if paramMsg("app.ios.install.link") != "app.ios.install.link">
            <p>${paramMsg("app.ios.install.link")}</p>
            </#if>
         </div>
      </#if>

      <div class="clear"></div>

   </#if>

</div>

<script type="text/javascript">//<![CDATA[
new Alfresco.cloud.component.AccountCompletion("${args.htmlid?js_string}").setOptions({
   username: <#if username??>"${username?js_string}"<#else>null</#if>,
   <#if passwordPolicy??>minPasswordLength: ${passwordPolicy.minLength},
   minPasswordUpper: ${passwordPolicy.minCharsUpper},
   minPasswordLower: ${passwordPolicy.minCharsLower},
   minPasswordNumeric: ${passwordPolicy.minCharsNumeric},
   minPasswordSymbols: ${passwordPolicy.minCharsSymbols},</#if>
   redirect: <#if redirect??>"${tenantContext}${redirect}"<#else>null</#if>,
   startpageIOS: <#if startpageIOS??>"${startpageIOS?js_string}"<#else>null</#if>,

   // Additional message params
   currentUsername: "${(user.id!"")?js_string}",
   inviterName: <#if inviterName??>"${inviterName?js_string}"<#else>null</#if>,
   inviteSiteTitle: <#if inviteSiteTitle??>"${inviteSiteTitle?js_string}"<#else>null</#if>,
   postLogoutRedirectUrl: <#if redirectPage??>"${url.context}${postLogoutRedirectPage}"<#else>null</#if>,
   instantRedirect: <#if instantRedirect??>"${instantRedirect}"<#else>null</#if>
}).setMessages(${messages});
//]]></script>
