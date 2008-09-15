<script type="text/javascript">//<![CDATA[
new Alfresco.UserWelcome("${args.htmlid}");
//]]></script>
<div class="dashlet user-welcome">
   <div class="title">${msg("header.userWelcome")}</div>
   <div class="body">
      <div class="detail-list-item-alt">
         <h4>${msg("header.userDashboard")}</h4>
         <div>${msg("text.userDashboard")}</div>
      </div>
      <div class="detail-list-item">
         <h4>${msg("header.customiseDashboard")}</h4>
         <div>${msg("text.customiseDashboard")}</div>
         <div><a href="${url.context}/page/customise-user-dashboard">${msg("link.customiseDashboard")}</a></div>
      </div>
      <div class="detail-list-item">
         <h4>${msg("header.userProfile")}</h4>
         <div>${msg("text.userProfile")}</div>
         <div><a href="${url.context}/page/user/${user.name?url}/profile">${msg("link.userProfile")}</a></div>
      </div>
      <#if sites?size &gt; 0>
         <div class="detail-list-item">
            <h4>${msg("header.mySites")}</h4>
            <div>${msg("text.mySites")}</div>
         <#list sites as site>
            <div class="site"><a href="${url.context}/page/site/${site.shortName}/dashboard">${site.title}</a></div>
         </#list>
         </div>
      </#if>
      <div class="detail-list-item last">
         <h4>${msg("header.createSite")}</h4>
         <div>${msg("text.createSite")}</div>
         <div><a id="${args.htmlid}-createSite-button" href="#">${msg("link.createSite")}</a></div>
      </div>


      <div class="detail-list-item last">
         <h4>Alfresco Widget</h4>
         <div>You want alerts from your site's calendar & activities, chat with online users and be able to upload files with desktop drag n drop? Try the Alfresco widget below.</div>
         <div style="text-align: center;">            
            <div id="flashcontent" style="width:100%; height:200px; text-align: center;">
                  <strong>Please upgrade your Flash Player</strong>
                  This is the content that would be shown if the user does not have Flash Player 6.0.65 or higher installed.
               </div>

               <script type="text/javascript">
                  // <![CDATA[        
                  <#assign baseUrl = "http://bobby:8080/${url.context}/widget"/>
                  // version 9.0.115 or greater is required for launching AIR apps.
                  var so = new SWFObject("${baseUrl}/AIRInstallBadge.swf", "Badge", "240", "200", "9.0.115", "#FFFFFF");
                  so.useExpressInstall('${baseUrl}/expressinstall.swf');
            
                  // these parameters are required for badge install:
                  so.addVariable("airversion", "1.0"); // version of AIR runtime required
                  so.addVariable("appname", "Alfresco Widget"); // application name to display to the user
                  so.addVariable("appurl", "${baseUrl}/AlfrescoWidget.air"); // absolute URL (beginning with http or https) of the application ".air" file

                  // these parameters are required to support launching apps from the badge (but optional for install):
                  so.addVariable("appid", "AlfrescoWidget"); // the qualified application ID (ex. com.gskinner.air.MyApplication)
                  so.addVariable("pubid", "Alfresco"); // publisher id

                  // this parameter is required in addition to the above to support upgrading from the badge:
                  so.addVariable("appversion", "0.99"); // AIR application version
                       
                  // these parameters are optional:
                  //so.addVariable("imageurl", "${baseUrl}/alfresco-widget-screenshot2.png"); // URL for an image (JPG, PNG, GIF) or SWF to display in the badge (205px wide, 170px high)
                  so.addVariable("appinstallarg", "installed from web"); // passed to the application when it is installed from the badge
                  so.addVariable("applauncharg", "launched from web"); // passed to the application when it is launched from the badge
                  so.addVariable("helpurl", "help.html"); // optional url to a page containing additional help, displayed in the badge's help screen
                  so.addVariable("hidehelp", "false"); // hides the help icon if "true"
                  so.addVariable("skiptransition", "true"); // skips the initial transition if "true"
                  so.addVariable("titlecolor", "#FFFFFF"); // changes the color of titles
                  so.addVariable("buttonlabelcolor", "#00AAFF"); // changes the color of the button label
                  so.addVariable("appnamecolor", "#00AAFF"); // changes the color of the application name if the image is not specified or loaded
                               
                  // these parameters allow you to override the default text in the badge:
                  // supported strings: str_error, str_err_params, str_err_airunavailable, str_err_airswf, str_loading, str_install, str_launch, str_upgrade, str_close, str_launching, str_launchingtext, str_installing, str_installingtext, str_tryagain, str_beta3, str_beta3text, str_help, str_helptext
                  so.addVariable("str_err_airswf", "<u>Running locally?</u><br/><br/>The AIR proxy swf won't load properly when this demo is run from the local file system."); // overrides the error text when the AIR proxy swf fails to load

                  so.write("flashcontent");

                  // ]]>
               </script>

         </div>
      </div>


   </div>
</div>
