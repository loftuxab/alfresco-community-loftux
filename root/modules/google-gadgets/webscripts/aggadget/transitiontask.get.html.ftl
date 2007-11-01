<#assign task=workflow.getTaskById(args.id)>

<html>
   <head> 
      <title>Transition Task</title>
      <link rel="stylesheet" href="${url.context}/css/gg.css" TYPE="text/css">
      <script type="text/javascript">
function goback()
{
   window.location="${args.returl}";
}
      </script>
   </head>
   
   <body>
      <div class="main">
         <div class="titlebar">Success</div>
         <div class="dialog">
            <div style="padding: 0 0 4 4">'${args.m}' executed for task '<#if task.description?exists>${task.description?html}<#else>${task.type?html}</#if>'</div>
            <div style="display:table"><input type="button" onclick="goback();" value="Continue"></div>
         </div>
      </div>
   </body>
</html>
