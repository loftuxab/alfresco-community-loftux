<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head> 
      <title>${title}</title> 
      <link rel="stylesheet" href="${url.context}/css/base.css" type="text/css"/>
      ${head}
   </head>
   <body class="yui-skin-sam">
      <div>
         <@region id="header" scope="global" protected=true/>
      </div>
      <div>
         <@region id="title" scope="template" protected=true />
      </div>      
      <div>
         <@region id="navigation" scope="template" protected=true />
      </div>
      <div>
         <@region id="doclib" scope="page">TODO: Bind doclib components</@region>
      </div>
      <div>
         <@region id="footer" scope="global" protected=true />
      </div>
   </body>
</html>