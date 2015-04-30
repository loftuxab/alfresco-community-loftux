<html>
<head>
   <title>Save config success</title>
</head>
<body>
   <#if (args.success!"")?matches("^[\\w\\d\\._]+$")>
   <script type="text/javascript">
    	${args.success}({
         success: true
      });
   </script>
   </#if>
</body>
</html>