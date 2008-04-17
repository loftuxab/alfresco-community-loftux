<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head> 
   <title>${title}</title> 
   <!-- Site-wide Assets -->
   <link rel="stylesheet" href="${url.context}/yui/reset-fonts-grids/reset-fonts-grids.css" type="text/css" />
   <link rel="stylesheet" href="${url.context}/themes/default/base.css" type="text/css" />
   <script type="text/javascript" src="${url.context}/yui/utilities/utilities.js"></script>

   <!-- Document Library YUI Assets -->
   <link rel="stylesheet" href="${url.context}/yui/resize/assets/skins/sam/resize.css" type="text/css" />
   <script type="text/javascript" src="${url.context}/yui/resize/resize-beta-min.js"></script>

   <!-- Document Library Assets -->
   <link rel="stylesheet" href="${url.context}/templates/documentlibrary/documentlibrary.css" type="text/css" />
   <script type="text/javascript" src="${url.context}/templates/documentlibrary/documentlibrary.js"></script>

   <!-- Component Assets -->
   ${head}

</head>  
<body class="yui-skin-sam">
   <div id="doc3">
      <div id="hd">
         <@region id="header" scope="global" protected=true />
         <@region id="title" scope="page" protected=true />
         <@region id="navigation" scope="page" protected=true />
      </div>
      <div id="bd">
         <div class="yui-t1" id="divDoclibWrapper">
            <div id="yui-main">
               <div class="yui-b" id="divDoclibDocs">
                  <@region id="documentlist" scope="template" protected=true />
               </div>
            </div>
            <div class="yui-b" id="divDoclibFilters">
               <@region id="filter" scope="template" protected=true />
               <@region id="tree" scope="template" protected=true />
               <@region id="packager" scope="template" protected=true />
            </div>
         </div>
      </div>
      <div id="ft">
         <@region id="footer" scope="global" protected=true />
      </div>
      <@region id="file-upload" scope="page"/>      
   </div>
</body>
</html>