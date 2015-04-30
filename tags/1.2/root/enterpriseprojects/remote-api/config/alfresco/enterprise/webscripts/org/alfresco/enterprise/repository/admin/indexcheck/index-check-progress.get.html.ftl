<#import "/org/alfresco/webscripts.lib.html.ftl" as wsLib/>
<#import "index-check.lib.html.ftl" as icLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Alfresco DM Index Check - progress (running on: ${ipAddress})</@wsLib.head>
  <body>
    <div>
    <@wsLib.header>Alfresco DM Index/Txn Consistency Check - progress (running on: <b>${ipAddress}</b>)</@wsLib.header>
    <br/>
${progress}
    <p/>
    <#if indexTxnInfo??>
    <b>Previous</b> <@icLib.reportDetails></@icLib.reportDetails>
    <#else>
    No previous report run (since server started)
    </#if>
    <p/>
    <a href="${url.serviceContext}/enterprise/admin/indexcheck">Back</a>
  </body>
</html>