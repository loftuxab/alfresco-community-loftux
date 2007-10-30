<html>
   <head> 
      <title>Uploaded File</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css">
   </head>
   
   <body>
      <div class="main">
         <div class="titlebar">Success</div>
         <div class="dialog">
            <div style="padding: 0 0 4 4">Uploaded: '${upload.name}' of size ${(upload.size/1000)?string("0.##")}&nbsp;KB</div>
            <div style="display:table"><input type="button" onclick="window.location='${returl}';" value="Continue"></div>
         </div>
      </div>
   </body>
</html>
