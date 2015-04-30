<html>
   <head>
      <script type="text/javascript">
      <#if idp??>
         (function()
         {
            window.onload = function ()
            {
               // User has already been logged out form Share and will be logged out from the IDP
               var form = document.getElementById("form");
               form.submit();
            };
         })();
      <#elseif error??>
         // Redirect user to the error page
         window.location.href = "${url.context}/page/message?text=${error}";
      </#if>
      </script>
   </head>
   <body>
      <#if idp??>
         <!-- idp.action is already encoded -->
         <form id="form" method="post" action="${idp.action}">
            <input type="hidden" name="SAMLResponse" value="${idp.SAMLResponse?html}" />
            <#if idp.RelayState??>
            <input type="hidden" name="RelayState" value="${idp.RelayState?html}" />
            </#if>
         </form>
      </#if>
   </body>
</html>

