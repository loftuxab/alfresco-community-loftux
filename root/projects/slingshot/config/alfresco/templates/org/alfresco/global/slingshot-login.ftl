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
      <form id="loginform" accept-charset="UTF-8" method="post" action="${url.context}/login" onsubmit="return alfLogin();">
         <fieldset>
            <div style="padding-top:96px">
               <span id="txt-username"></span>
            </div>
            <div style="padding-top:4px">
               <input type="text" id="username" name="username" maxlength="256" style="width:200px"/>
            </div>
            <div style="padding-top:12px">
               <span id="txt-password"></span>
            </div>
            <div style="padding-top:4px">
               <input type="password" id="password" name="password" maxlength="256" style="width:200px"/>
            </div>
            <div style="padding-top:16px">
               <input type="submit" id="btn-login" class="login-button" />
            </div>
            <div style="padding-top:32px">
               <span class="login-copyright">
                  &copy; 2005-2008 Alfresco Software Inc. All rights reserved.
               </span>
            </div>
            <input type="hidden" id="success" name="success" value="${successUrl}"/>
            <input type="hidden" name="failure" value="<#assign link><@link pageType='login'/></#assign>${url.servletContext}${link?html}&amp;error=true"/>
         </fieldset>
      </form>
   </div>
   
   <script type="text/javascript">//<![CDATA[
   function alfLogin()
   {
      YAHOO.util.Dom.get("btn-login").setAttribute("disabled", true);
      return true;
   }
   
   YAHOO.util.Event.onContentReady("alflogin", function()
   {
      var Dom = YAHOO.util.Dom;
      
      // Prevent the Enter key from causing a double form submission
      var form = Dom.get("loginform");
      // add the event to the form and make the scope of the handler this form.
      YAHOO.util.Event.addListener(form, "submit", this._submitInvoked, this, true);
      var fnStopEvent = function(id, keyEvent)
      {
         if (form.getAttribute("alflogin") == null)
         {
            form.setAttribute("alflogin", true);
         }
         else
         {
            form.attributes.action.nodeValue = "";
         }
      }
      
      var enterListener = new YAHOO.util.KeyListener(form,
      {
         keys: YAHOO.util.KeyListener.KEY.ENTER
      }, fnStopEvent, "keydown");
      enterListener.enable();
      
      // set I18N labels
      Dom.get("txt-username").innerHTML = Alfresco.util.message("label.username") + ":";
      Dom.get("txt-password").innerHTML = Alfresco.util.message("label.password") + ":";
      Dom.get("btn-login").value = Alfresco.util.message("button.login");
      
      // generate and display main login panel
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
      
      Dom.get("success").value += window.location.hash;
      Dom.get("username").focus();
   });
   
   <#if url.args["error"]??>
   Alfresco.util.PopupManager.displayPrompt(
      {
         title: Alfresco.util.message("message.loginfailure"),
         text: Alfresco.util.message("message.loginautherror")
      });
   </#if>
   //]]></script>
</@>
</body>
</html>