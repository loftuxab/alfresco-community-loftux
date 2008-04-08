<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head> 
      <title>${title}</title> 
      <link rel="stylesheet" href="${url.context}/css/basic.css" type="text/css"/>
      <!--${head}-->
   </head>
   <body>
      <div>
         <@region id="header" scope="global" protected=true>
            Default region content (only see this if component cannot be resolved!)
         </@region>
      </div>
      <div>
         <@region id="userdashboard-title" scope="template" protected=true />
      </div>
      <div>
         <@region id="mysites-dashlet" scope="page" />
      </div>
      <div>
         <@region id="footer" scope="global" protected=true />
      </div>
   </body>
</html>