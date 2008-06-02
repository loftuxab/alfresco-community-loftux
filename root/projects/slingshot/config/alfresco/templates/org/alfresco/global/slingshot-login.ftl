<#import "../import/alfresco-template.ftl" as template />
<@template.header>
   <link rel="stylesheet" type="text/css" href="${url.context}/templates/login/login.css" />
   <style type="text/css">
      .login-logo
      {
         position: absolute;
         top: 16px;
         height: 64px;
         width: 450px;
         background: transparent url("${url.context}/themes/${theme}/images/logo.png") no-repeat;
      }
   </style>
</@>
<@template.body>
   <div id="alflogin" class="login-panel">
      <div class="login-logo"></div>
      <form accept-charset="UTF-8" method="post" action="${url.context}/login">
         <fieldset>
            <div style="padding-top:96px">
               <span>Username:</span>
            </div>
            <div style="padding-top:4px">
               <input type="text" id="username" name="username" maxlength="256" style="width:200px"/>
            </div>
            <div style="padding-top:12px">
               <span>Password:</span>
            </div>
            <div style="padding-top:4px">
               <input type="password" id="password" name="password" maxlength="256" style="width:200px"/>
            </div>
            <div style="padding-top:16px">
               <input type="submit" value="Login"/>
            </div>
            <div style="padding-top:32px">
               <span class="login-copyright">
                  &copy; 2005-2008 Alfresco Software Inc. All rights reserved.
               </span>
            </div>
            <input type="hidden" name="success" value="${successUrl}"/>
            <input type="hidden" name="failure" value="<@link pageType='login'/>"/>
         </fieldset>
      </form>
   </div>
   
   <script type="text/javascript">//<![CDATA[
   YAHOO.util.Event.onContentReady("alflogin", function()
   {
      var panel = new YAHOO.widget.Overlay(YAHOO.util.Dom.get("alflogin"), 
      {
         modal: false,
         draggable: false,
         fixedcenter: true,
         close: false,
         visible: true,
         iframe: false
      });
      panel.render(document.body);
      
      YAHOO.util.Dom.get("username").focus();
   });
   //]]></script>
</@>