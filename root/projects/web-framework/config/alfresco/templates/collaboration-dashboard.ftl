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
         <table cellpadding="10" width="100%">
           <tr valign="top">
             <td><@region id="colleagues" scope="page"/></td>
             <td><@region id="simple-dashlet" scope="page"/></td>
             <td><@region id="site-profile" scope="page"/></td>
           </tr>
           <tr valign="top">
             <td><@region id="simple-dashlet" scope="page"/></td>
             <td><@region id="simple-dashlet" scope="page"/></td>
             <td><@region id="simple-dashlet" scope="page"/></td>
           </tr>
           <tr valign="top">
             <td><@region id="simple-dashlet" scope="page"/></td>
             <td><@region id="doclist" scope="page"/></td>
             <td><@region id="simple-dashlet" scope="page"/></td>
           </tr>
         </table>         
      </div>
      <div>
         <@region id="footer" scope="global" protected=true />
      </div>
   </body>
</html>