<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head> 
      <title>${title}</title> 
      <link rel="stylesheet" href="${url.context}/css/basic.css" type="text/css"/>
      <!-- ${header} -->
   </head>
   <body>
      <div style="border: 1px solid #dddddd; padding:4px">
         <#include "/[header]" parse=false>
      </div>
      A header component is above this plain text.
      <br/>
      Now another component appears...
      <div style="border: 1px solid #dddddd; padding:4px">
         <#include "/[comp01]" parse=false>
      </div>
      Additional plain text, and another component...
      <div style="border: 1px solid #dddddd; padding:4px">
         <#include "/[comp02]" parse=false>
      </div>
      More plain text, and another component...
      <div style="border: 1px solid #dddddd; padding:4px">
         <#include "/[comp03]" parse=false>
      </div>
   </body>
</html>