<!-- Document List Assets -->
<link rel="stylesheet" type="text/css" href="${url.context}/components/documentlibrary/documentlist.css" />
<script type="text/javascript" src="${url.context}/components/documentlibrary/documentlist.js"></script>
<script type="text/javascript">//<![CDATA[
   new Alfresco.DocumentList("${htmlid}").setOptions(
   {
      initialPath: "${url.args["path"]!""}"
   });
//]]></script>
