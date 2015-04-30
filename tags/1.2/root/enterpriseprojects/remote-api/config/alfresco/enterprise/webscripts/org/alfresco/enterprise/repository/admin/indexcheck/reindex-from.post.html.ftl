<#import "/org/alfresco/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Alfresco DM Index Check - re-index started ... (running on: ${ipAddress})</@wsLib.head>
  <body>
    <div>
    <@wsLib.header>Alfresco DM Index/Txn Consistency Check - re-index started ... (running on: <b>${ipAddress}</b>)</@wsLib.header>
    <br/>
Alfresco DM re-index started ... <a href="${url.serviceContext}/enterprise/admin/reindex/adm/progress">get progress</a> to see if re-index is still in progress (or completed)
    <p/>
    <a href="${url.serviceContext}/enterprise/admin/indexcheck">Back</a>
  </body>
</html>