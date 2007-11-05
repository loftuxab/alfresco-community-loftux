<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
   <head> 
      <title>Deleted ${name}</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css"/>
      <script type="text/javascript">
function goback()
{
   window.location="${returl}";
}
      </script>
   </head>
   
   <body>
      <div class="main">
         <div class="titlebar">Success</div>
         <div class="dialog">
            <div style="padding: 0px 0px 4px 4px">Deleted item: '${name}'</div>
            <div><input style="width:auto" type="button" onclick="goback();" value="Continue"/></div>
         </div>
      </div>
   </body>
</html>