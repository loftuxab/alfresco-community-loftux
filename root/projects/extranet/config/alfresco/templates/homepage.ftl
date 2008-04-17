<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head> 
      <title>${title}</title> 
      <link rel="stylesheet" href="${url.context}/css/basic.css" type="text/css"/>      

      <@head />

   </head>
   <body>
   
      This is the top of the page

      <br/>
      <br/>
      
      There is a site-scoped region below here but it is unbound:
      
      <div style="border: 1px black solid; padding: 10px">
         <@region id="header" scope="site" access="protected">
            Default region content (only see this if component cannot be resolved!)
         </@region>
      </div>
      
      <br/>

      Here is a snazzy web component bound in (template scope):

      <div>
         <@region id="content" scope="template" />
      </div>
      
      <br/>
      
      Here is a 
      <@anchor id="page.home">
       link back to this page
      </@anchor>

      <br/>
      
      Here is a       
      <@anchor id="home" target="_blank">
       link back to this page (new window)
      </@anchor>
      
      <br/>
      
      Here is a
      <@anchor id="workspace://SpacesStore/1234567890">
      	link to an imaginary project
      </@anchor>      
      
   </body>
</html>
