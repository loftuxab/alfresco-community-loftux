<#include "include/mobile.ftl" />
<@templateHeader>
</@>

<@templateBody>
   <img src="${url.context}/themes/${theme}/images/login-logo.png" alt="" />
      <form id="loginform" accept-charset="UTF-8" method="post" action="/mobile/login">
         <fieldset>
             <label id="txt-username" for="username">Username</label>
             <input type="text" id="username" name="username" maxlength="256" />
             <label id="txt-password" for="password">Password</label>
             <input type="password" id="password" name="password" maxlength="256" />
             <input type="checkbox" name="remember" value="" id="remember"><label for="remember">Remember Me</label>
             <input type="submit" id="btn-login" class="button" value="Login" />
             <input type="hidden" id="success" name="success" value="/mobile/p/"/>
             <input type="hidden" name="failure" value="/mobile/p?f=default&amp;pt=login&amp;error=true"/>
         </fieldset>
      </form>
   
   <script type="text/javascript">//<![CDATA[
<#if url.args["error"]??>
   alert("Login failed.");
</#if>
   //]]></script>
</@>