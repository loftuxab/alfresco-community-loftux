<#include "include/awe.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/login/login.css" />
   <@link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/login.css" />
</@>
<@templateBody>
   <div id="awe-login-wrapper">
      <@region id="login" scope="template" protected=false />
   </div>
</@>
</body>
</html>