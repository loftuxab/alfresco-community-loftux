<#--
   Template "header" macro.
   Includes preloaded YUI assets and essential site-wide libraries.
-->
<#macro header>
<#assign theme="sam">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <title>${title}</title> 

<!-- Site-wide YUI Assets -->
   <link rel="stylesheet" type="text/css" href="${url.context}/yui/reset-fonts-grids/reset-fonts-grids.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/yui/assets/skins/${theme}/skin.css" />

<#-- Selected components preloaded here for better UI experience. -->
<!-- Common YUI components -->
   <script type="text/javascript" src="${url.context}/yui/utilities/utilities.js"></script>
   <script type="text/javascript" src="${url.context}/yui/button/button-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/container/container-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/menu/menu-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/json/json-min.js"></script>
   <script type="text/javascript" src="${url.context}/js/bubbling.v1.5.0.js"></script>

<!-- Site-wide Common Assets -->
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/base.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/controls.css" />
   <script type="text/javascript" src="${url.context}/js/alfresco.js"></script>
   <script type="text/javascript">//<![CDATA[
      Alfresco.constants.PROXY_URI = window.location.protocol + "//" + window.location.host + "/${url.context}/proxy?endpoint=",
      Alfresco.constants.THEME = "${theme}";
      Alfresco.constants.URL_CONTEXT = "${url.context}/";
      Alfresco.constants.URL_SERVICECONTEXT = "${url.context}/service/";
   //]]></script>

<!-- Component Assets -->
${head}

<!-- Template Assets -->
<#nested>
</head>
</#macro>


<#--
   Template "body" macro.
   Pulls in main template body.
-->
<#macro body>
<body class="yui-skin-${theme}">
<#-- Template-specific body markup -->
<#nested>
<#-- This function call MUST come after all other component includes. -->
   <script type="text/javascript">//<![CDATA[
      Alfresco.util.YUILoaderHelper.loadComponents();
   //]]></script>
</body>
</html>
</#macro>