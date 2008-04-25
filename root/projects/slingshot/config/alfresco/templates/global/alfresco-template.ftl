<#--
   Template "header" macro
   
   Includes preloaded YUI assets and essential site-wide libraries
-->
<#macro header>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
   <title>${title}</title> 

<!-- Site-wide YUI Assets -->
   <link rel="stylesheet" type="text/css" href="${url.context}/yui/reset-fonts-grids/reset-fonts-grids.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/yui/assets/skins/sam/skin.css" />

<!-- Common YUI components -->
<#-- Selected components preloaded here for better UI experience.
     Modifications to <script> includes should be carefully reflected in the preloaded() call below.
     See yuiloader.js for official component names and dependencies.
-->
   <script type="text/javascript" src="${url.context}/yui/utilities/utilities.js"></script>
   <script type="text/javascript" src="${url.context}/yui/button/button-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/container/container_core-min.js"></script>
   <script type="text/javascript" src="${url.context}/yui/menu/menu-min.js"></script>

<!-- Site-wide Common Assets -->
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/default/base.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/default/controls.css" />
   <script type="text/javascript" src="${url.context}/js/alfresco.js"></script>
   <script type="text/javascript">//<![CDATA[
     Alfresco.constants.URL_CONTEXT = "${url.context}/";
     Alfresco.util.YUILoaderHelper.preloaded(["yahoo", "event", "dragdrop", "animation", "dom", "connection", "element", "yahoo-dom-event", "get", "yuiloader", "yuiloader-dom-event", "utilities", "button", "containercore", "menu"]);
   //]]></script>

<!-- Component Assets -->
${head}

<!-- Template Assets -->
<#nested>

<!-- Kick-off the YUILoader -->
   <script type="text/javascript">//<![CDATA[
      Alfresco.util.YUILoaderHelper.loadComponents();
   //]]></script>
</head>
</#macro>


<#--
   Template "body" macro
   
   Pulls in main template body
-->
<#macro body>
<#-- Template-specific body markup -->
<#nested>
</html>
</#macro>