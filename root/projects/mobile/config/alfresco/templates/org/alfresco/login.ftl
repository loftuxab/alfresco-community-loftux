<#include "include/mobile.ftl" />
<@templateHeader>
</@>

<@templateBody>
   <div id="alf-login">
      <form id="loginform" accept-charset="UTF-8" method="post" action="${url.context}/login">
         <fieldset>
            <div style="padding-top:4px">
               <label id="txt-username" for="username">Username</label>
            </div>
            <div style="padding-top:4px">
               <input type="text" id="username" name="username" maxlength="256" style="width:200px"/>
            </div>
            <div style="padding-top:12px">
               <label id="txt-password" for="password">Password</label>
            </div>
            <div style="padding-top:4px">
               <input type="password" id="password" name="password" maxlength="256" style="width:200px"/>
            </div>
            <div style="padding-top:16px">
               <input type="submit" id="btn-login" class="login-button" value="Login" />
            </div>
            <input type="hidden" id="success" name="success" value="${successUrl}"/>
            <input type="hidden" name="failure" value="<#assign link><@pagelink pageType='login'/></#assign>${url.servletContext}${link?html}&amp;error=true"/>
         </fieldset>
      </form>
   </div>
   
   <script type="text/javascript">//<![CDATA[
<#if url.args["error"]??>
   alert("Login failed.");
</#if>
   //]]></script>
</@>