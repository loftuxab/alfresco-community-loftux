<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <head> 
      <title>Page Test 01</title> 
      <link rel="stylesheet" href="${url.context}/css/basic.css" TYPE="text/css"/>
   </head>
   <body>
      Some plain text here.
      <br/>
      Now a component appears...
      <div style="border: 1px solid #dddddd; padding:4px">
         <#include "/[test01]" parse=false>
      </div>
      Additional plain text.
   </body>
</html>