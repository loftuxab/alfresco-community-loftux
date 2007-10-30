<html>
   <head>
      <title>Upload File</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css">
      <style type="text/css">
td
{
   font-family: Trebuchet MS, Arial, sans-serif;
   font-size: 12px;
   color: #515D6B;
}
      </style>
   </head>
   
   <body>
   
   <div class="main">
      <div class="titlebar">Upload a file</div>
      <div class="dialog">
         <table width="100%">
            <form action="${url.service}" method="post" enctype="multipart/form-data" accept-charset="UTF-8">
            <tr><td>File:&nbsp;</td><td><input type="file" name="file" size="35"></td></tr>
            <tr><td>Title:&nbsp;</td><td><input name="title" maxlength="1024"></td></tr>
            <tr><td>Description:&nbsp;</td><td><input name="desc" maxlength="1024"></td></tr>
            <tr>
               <td colspan=2>
                  <input type="submit" name="submit" value="Upload" style="font-weight:bold;width:60px">
                  <input type="button" name="cancel" value="Cancel" style="font-weight:bold;width:60px;margin-left:12px;" onclick="javascript:history.back();">
               </td>
            </tr>
            <input type="hidden" name="fdrnodeid" value="${args.fdrnodeid}">
            <input type="hidden" name="returl" value="${args.returl}">
            </form>
         </table>
      </div>
   </div>
   
   </body>
</html>