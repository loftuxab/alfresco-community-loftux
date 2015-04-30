<#assign el=args.htmlid?html/>

<@markup id="css" >
   <#-- CSS Dependencies -->
</@>

<@markup id="js">
   <#-- This webscript is added to the "redirect" template which doesn't bring in any resources by itself to be as quick as possible -->
   <@script type="text/javascript" src="${url.context}/res/yui/yahoo/yahoo.js" group="saml"/>
   <@script type="text/javascript" src="${url.context}/res/yui/cookie/cookie.js" group="saml"/>
</@>

<@markup id="widgets">
   <@inlineScript group="saml">
      <#if idp??>
         (function()
         {
            window.onload = function ()
            {
               // First store the redirectPage as a cookie so we know where to redirect the user when coming back from the idp
               var ORG_ALFRESCO_SHARE_SAML_COOKIE = "org.alfresco.share.saml.loginRedirectPage";
               YAHOO.util.Cookie.set(ORG_ALFRESCO_SHARE_SAML_COOKIE, "${redirectPage?js_string}", {
                  path: "${applicationContext?js_string}"
               });

               // Redirect user to the IDP with our signed & base64 encoded SAML AuthnRequest
               var form = document.getElementById("${args.htmlid?js_string}-form");
               form.submit();

               // Once user is identified at the IDP and redirected back to Share the samlAuthnRequestController will take over
            };
         })();
      <#elseif error?? && redirectPage??>
         (function()
         {
            window.location.href = "${applicationContext?js_string}/${context.attributes["org.alfresco.cloud.tenant.name"]?js_string}/page/${redirectPage?js_string}";
         })();

      <#else>

         (function()
         {
            window.location.href = "${applicationContext?js_string}";
         })();

      </#if>
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if idp??>
         <!-- idp.action is already encoded -->
         <form id="${el}-form" method="post" action="${idp.action}">
            <input type="hidden" name="SAMLRequest" value="${idp.SAMLRequest?html}" />
            <input type="hidden" name="Signature" value="${idp.Signature?html}"/>
            <input type="hidden" name="SigAlg" value="${idp.SigAlg?html}"/>
            <input type="hidden" name="KeyInfo" value="${idp.KeyInfo?html}"/>
         </form>
      </#if>
   </@>
</@>
