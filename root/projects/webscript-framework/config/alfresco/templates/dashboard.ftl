<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head> 
      <title>${title}</title> 
      <link rel="stylesheet" href="${url.context}/css/base.css" type="text/css"/>
      <!--${head}-->
   </head>
   <body>
      <div>
         <@region id="header" scope="global" protected=true>
            Default region content (only see this if component cannot be resolved!)
         </@region>
      </div>
      A header component is above this plain text.
      <br/>
      Now a nav component appears...
      <div>
         <@region id="nav" scope="global" protected=true />
      </div>
      Additional plain text, and another component...
      <div>
         <@region id="main" scope="page" protected=true />
      </div>
      More plain text, and another component...
      <div>
         <#--Example of how you might configure a template with template config -->
         <#if columns[0].enabled>
         <@region id="column1" scope="template" />
         </#if>
         
         <#if columns[1].enabled>
         <@region id="column2" scope="template" />
         </#if>
         
         <#-- example of dynamic regions (more js logic for the template generates the "regions" object) -->
         <#--
         <#list regions as r>
            <@region id="${r.id}" scope="${r.scope}" protected=false></@region>
         </#list>
         -->
      </div>
   </body>
</html>