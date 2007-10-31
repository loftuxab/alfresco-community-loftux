<html>
   <head> 
      <title>Deleted ${name}</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css">
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
            <div style="padding: 0 0 4 4">Deleted item: '${name}'</div>
            <div style="display:table"><input type="button" onclick="goback();" value="Continue"></div>
         </div>
      </div>
   </body>
</html>
