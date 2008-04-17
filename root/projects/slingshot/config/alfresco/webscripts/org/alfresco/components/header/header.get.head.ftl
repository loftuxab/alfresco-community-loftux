<!-- Site-wide YUI Assets -->
<link rel="stylesheet" href="${url.context}/yui/reset-fonts-grids/reset-fonts-grids.css" type="text/css" />
<link rel="stylesheet" href="${url.context}/themes/default/base.css" type="text/css" />
<script type="text/javascript" src="${url.context}/yui/utilities/utilities.js"></script>

<!-- Site-wide Common Assets -->
<script type="text/javascript">//<![CDATA[
  /* Ensure Alfresco root object exists */
  var Alfresco = (typeof Alfresco == "undefined" || !Alfresco ? {} : Alfresco);
//]]></script>

<!-- Header YUI Assets -->
<link rel="stylesheet" type="text/css" href="${url.context}/yui/button/assets/skins/sam/button.css" />
<link rel="stylesheet" type="text/css" href="${url.context}/yui/menu/assets/skins/sam/menu.css" />
<script type="text/javascript" src="${url.context}/yui/container/container-min.js"></script>
<script type="text/javascript" src="${url.context}/yui/menu/menu-min.js"></script>
<script type="text/javascript" src="${url.context}/yui/button/button-min.js"></script>

<!-- Header Assets -->
<link rel="stylesheet" type="text/css" href="${url.context}/components/header/header.css" />
<script type="text/javascript" src="${url.context}/components/header/header.js"></script>
<script type="text/javascript">//<![CDATA[
   Alfresco.Header.ID = "${htmlid}";
//]]></script>
