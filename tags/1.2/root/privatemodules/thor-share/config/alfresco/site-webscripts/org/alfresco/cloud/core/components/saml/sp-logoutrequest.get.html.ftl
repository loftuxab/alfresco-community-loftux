<#assign el=args.htmlid?html/>

<@markup id="css" >
   <#-- CSS Dependencies -->
</@>

<@markup id="js">
   <#-- This webscript is added to the "redirect" template which doesn't bring in any resources by itself to be as quick as possible -->
</@>

<@markup id="widgets">
   <@inlineScript group="saml">
      <#if idp??>
         (function()
         {
            window.onload = function ()
            {
               // Redirect user to the IDP with our signed & base64 encoded SAML LogoutRequest
               var form = document.getElementById("${args.htmlid?js_string}-form");
               form.submit();

               // Once user is identified at the IDP and redirected back to Share the samlLogoutRequestController will take over
            };
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
            <!-- Value is the key to the message string shown to the user. -->
            <input type="hidden" name="RelayState" value="saml.idp-logoutresponse.success.text" />
         </form>
      <#elseif error??>
         ${msg("error")}
      </#if>
   </@>
</@>
