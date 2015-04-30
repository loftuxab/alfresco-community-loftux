<#include "/org/alfresco/components/component.head.inc">

<@markup id="alfresco-cloud-resources" action="after" target="resources">

   <#-- DC: Using checksumResource directly, as rendering of @link not working here -->
   <link rel="stylesheet" type="text/css" href="<@checksumResource src="${url.context}/res/cloud/css/alfresco-cloud.css" parameter="checksum"/>" />

   <script type="text/javascript">
      // Reset resource url to "-default-" tenant so we don't reload resources when switching tenants
      var tokens = Alfresco.constants.URL_RESCONTEXT.match(/([^\/]+)/g);
      if (tokens && tokens.length == 3)
      {
         Alfresco.constants.URL_RESCONTEXT = '/' + tokens[0] + '/-default-/' + tokens[2] + '/';
      }
   </script>

   <@script type="text/javascript" src="${url.context}/res/cloud/js/alfresco-cloud.js"></@script>
   <script type="text/javascript">
      Alfresco.cloud.constants.CURRENT_TENANT = "${context.attributes["org.alfresco.cloud.tenant.name"]?js_string}";
      <#if config.scoped["Social"]["quickshare"].getChildValue("url")??>
      Alfresco.constants.QUICKSHARE_URL = "${(config.scoped["Social"]["quickshare"]).getChildValue("url")?replace("{context}", url.context?matches("/[^/]+")[0])?js_string}";
      </#if>
   </script>
   
   <#assign google_analytics = config.scoped["Cloud"]["google-analytics"]!>
   <#if google_analytics.getChildValue??>
      <#assign tracking_code = google_analytics.getChildValue("tracking-code")!"">
   </#if>
   <#if tracking_code??>
       <script type="text/javascript">
          var _gaq = _gaq || [];
          _gaq.push(['_setAccount', '${tracking_code}']);
          _gaq.push(['_trackPageview']);
    
          (function() {
             var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
             ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
             var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
          })();
      </script>
   </#if>
   
   <#assign mix_panel = config.scoped["Cloud"]["mix-panel"]!>
   <#if mix_panel.getChildValue??>
      <#assign mix_panel_id = mix_panel.getChildValue("mix-panel-id")!"">
   </#if>
   <#if mix_panel_id??>
       <!-- start Mixpanel -->
       <script type="text/javascript">(function(d,c){var a,b,g,e;a=d.createElement("script");a.type="text/javascript";a.async=!0;a.src=("https:"===d.location.protocol?"https:":"http:")+'//api.mixpanel.com/site_media/js/api/mixpanel.2.js';b=d.getElementsByTagName("script")[0];b.parentNode.insertBefore(a,b);c._i=[];c.init=function(a,d,f){var b=c;"undefined"!==typeof f?b=c[f]=[]:f="mixpanel";g="disable track track_pageview track_links track_forms register register_once unregister identify name_tag set_config".split(" "); 
          for(e=0;e<g.length;e++)(function(a){b[a]=function(){b.push([a].concat(Array.prototype.slice.call(arguments,0)))}})(g[e]);c._i.push([a,d,f])};window.mixpanel=c})(document,[]); 
          mixpanel.init("${mix_panel_id}");
      </script>
      <!-- end Mixpanel --> 
   </#if>
   
   <#assign app_dynamics = config.scoped["Cloud"]["app-dynamics"]!>
   <#if app_dynamics.getChildValue??>
      <#assign app_dynamics_key = app_dynamics.getChildValue("app-dynamics-key")!"">
   </#if>
   <#if app_dynamics_key??>
      <!-- start AppDynamics -->
      <script type="text/javascript">
         window["adrum-app-key"] = "${app_dynamics_key}";
         window["adrum-start-time"] = new Date().getTime();
      </script>
      <script src="${url.context}/res/cloud/js/adrum.js"></script>
      <!-- end AppDynamics --> 
   </#if>

</@markup>
