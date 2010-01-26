<#import "../import/alfresco-common.ftl" as common />

<#-- Global flags retrieved from web-framework-config-application -->
<#assign DEBUG=(common.globalConfig("client-debug", "false") = "true")>
<#assign AUTOLOGGING=(common.globalConfig("client-debug-autologging", "false") = "true")>
<#-- allow theme to be specified in url args - helps debugging themes -->
<#assign theme = (page.url.args.theme)!theme />

<#-- Look up page title from message bundles where possible -->
<#assign pageTitle = page.title />
<#if page.titleId??>
   <#assign pageTitle = (msg(page.titleId))!page.title>
</#if>
<#if context.properties["page-titleId"]??>
   <#assign pageTitle = msg(context.properties["page-titleId"])>
</#if>

<#--
   JavaScript minimisation via YUI Compressor.
-->
<#macro script type src>
   <script type="${type}" src="${DEBUG?string(src, src?replace(".js", "-min.js"))}"></script>
</#macro>
<#--
   Stylesheets gathered and rendered using @import to workaround IEBug KB262161
-->
<#assign templateStylesheets = []>
<#macro link rel type href>
   <#assign templateStylesheets = templateStylesheets + [href]>
</#macro>
<#macro renderStylesheets>
   <style type="text/css" media="screen">
   <#list templateStylesheets as href>
      @import "${href}";
   </#list>
   </style>
</#macro>

<#--
   Template "templateHeader" macro.
   Includes preloaded YUI assets and essential site-wide libraries.
-->                                                                           
<#macro templateHeader doctype="strict">
   <#if doctype = "strict">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
   <#else>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
   </#if>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <title>Alfresco Share &raquo; ${pageTitle}</title>
   <meta http-equiv="X-UA-Compatible" content="Edge" />

<!-- Shortcut Icons -->
   <link rel="shortcut icon" href="${url.context}/favicon.ico" type="image/vnd.microsoft.icon" /> 
   <link rel="icon" href="${url.context}/favicon.ico" type="image/vnd.microsoft.icon" />

<!-- Site-wide YUI Assets -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/yui/reset-fonts-grids/reset-fonts-grids.css" />
   <#if theme = 'default'>
      <@link rel="stylesheet" type="text/css" href="${url.context}/yui/assets/skins/default/skin.css" />
   <#else>
      <@link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/yui/assets/skin.css" />   
   </#if>
<#-- Selected components preloaded here for better UI experience. -->
<#if DEBUG>
   <script type="text/javascript" src="${url.context}/js/log4javascript.v1.4.1.js"></script>
<!-- Common YUI components: DEBUG -->
   <script type="text/javascript" src="${url.context}/yui/yahoo/yahoo-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/event/event-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/dom/dom-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/dragdrop/dragdrop-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/animation/animation-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/logger/logger-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/connection/connection-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/element/element-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/get/get-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/yuiloader/yuiloader-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/button/button-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/container/container-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/menu/menu-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/json/json-debug.js"></script>
   <script type="text/javascript" src="${url.context}/yui/selector/selector-debug.js"></script>
<!-- YUI Patches -->
   <script type="text/javascript" src="${url.context}/yui/yui-patch.js"></script>
   <script type="text/javascript">//<![CDATA[
      YAHOO.util.Event.throwErrors = true;
   //]]></script>
<#else>
<!-- Common YUI components: RELEASE concatenated -->
   <script type="text/javascript" src="${url.context}/js/yui-common.js"></script>
</#if>

<!-- Site-wide Common Assets -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/css/base.css" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/css/yui-layout.css" />   
   <@link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/presentation.css" />
   <@script type="text/javascript" src="${url.context}/js/bubbling.v2.1.js"></@script>
   <@script type="text/javascript" src="${url.context}/js/flash/AC_OETags.js"></@script>
   <#-- NOTE: Do not attempt to load -min.js version of messages.js -->
   <script type="text/javascript" src="${url.context}/service/messages.js?locale=${locale}"></script>
   <script type="text/javascript">//<![CDATA[
      Alfresco.constants = Alfresco.constants || {};
      Alfresco.constants.DEBUG = ${DEBUG?string};
      Alfresco.constants.AUTOLOGGING = ${AUTOLOGGING?string};
      Alfresco.constants.PROXY_URI = window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco/";
      Alfresco.constants.PROXY_URI_RELATIVE = "${url.context}/proxy/alfresco/";
      Alfresco.constants.PROXY_FEED_URI = window.location.protocol + "//" + window.location.host + "${url.context}/proxy/alfresco-feed/";
      Alfresco.constants.THEME = "${theme}";
      Alfresco.constants.URL_CONTEXT = "${url.context}/";
      Alfresco.constants.URL_PAGECONTEXT = "${url.context}/page/";
      Alfresco.constants.URL_SERVICECONTEXT = "${url.context}/service/";
      Alfresco.constants.URL_FEEDSERVICECONTEXT = "${url.context}/feedservice/";
      Alfresco.constants.USERNAME = "${user.name!""}";
   //]]></script>
   <@script type="text/javascript" src="${url.context}/js/alfresco.js"></@script>
   <@script type="text/javascript" src="${url.context}/js/forms-runtime.js"></@script>
   <@common.uriTemplate />
   <@common.htmlEditor htmlEditor="tinyMCE"/>

<!-- Template Assets -->
<#nested>
<@renderStylesheets />

<!-- Component Assets -->
${head}

<!-- MSIE CSS fix overrides -->
   <!--[if lt IE 7]><link rel="stylesheet" type="text/css" href="${url.context}/css/ie6.css" /><![endif]-->
   <!--[if IE 7]><link rel="stylesheet" type="text/css" href="${url.context}/css/ie7.css" /><![endif]-->
</head>
</#macro>


<#--
   Template "templateHtmlEditorAssets" macro.
   Loads wrappers for Rich Text editors.
-->
<#macro templateHtmlEditorAssets>
<!-- HTML Editor Assets -->
   <#-- NOTE: Do not attempt to load -min.js version of tiny_mce/tiny_mce.js -->
   <script type="text/javascript" src="${page.url.context}/modules/editors/tiny_mce/tiny_mce.js"></script>
   <@script type="text/javascript" src="${page.url.context}/modules/editors/tiny_mce.js"></@script>
   <@script type="text/javascript" src="${page.url.context}/modules/editors/yui_editor.js"></@script>
</#macro>


<#--
   Template "templateBody" macro.
   Pulls in main template body.
-->
<#macro templateBody>
<body id="Share" class="yui-skin-${theme}">
   <div class="sticky-wrapper">
      <div id="doc3">
<#-- Template-specific body markup -->
<#nested>
      </div>
      <div class="sticky-push"></div>
   </div>
</#macro>


<#--
   Template "templateFooter" macro.
   Pulls in template footer.
-->
<#macro templateFooter>
   <div class="sticky-footer">
<#-- Template-specific footer markup -->
<#nested>
   </div>
<#-- This function call MUST come after all other component includes. -->
   <div id="alfresco-yuiloader"></div>
   <script type="text/javascript">//<![CDATA[
      Alfresco.util.YUILoaderHelper.loadComponents();
   //]]></script>
</body>
</html>
</#macro>