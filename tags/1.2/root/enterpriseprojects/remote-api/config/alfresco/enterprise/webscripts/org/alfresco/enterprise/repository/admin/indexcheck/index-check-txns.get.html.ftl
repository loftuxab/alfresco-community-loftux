<#import "/org/alfresco/webscripts.lib.html.ftl" as wsLib/>
<#import "index-check.lib.html.ftl" as icLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Alfresco DM Index Check - check txns from a point in time (running on: ${ipAddress})</@wsLib.head>
  <body>
    <div>
    <@wsLib.header>Alfresco DM Index/Txn Consistency Check - check txns from a point in time (running on: <b>${ipAddress}</b>)</@wsLib.header>
    <br/>
<#if indexTxnInfo.missingCount == 0> 
Alfresco ADM Index Check - ${indexTxnInfo.processedCount?c} processed transaction ids are IN-SYNC with local indexes
    <#else>
Alfresco ADM Index Check - ${indexTxnInfo.missingCount?c} transaction ids are OUT-OF-SYNC with local indexes (out of ${indexTxnInfo.processedCount?c} processed)
    </#if>
    <p/>
    <@icLib.reportDetails></@icLib.reportDetails>
    <p/>
    <a href="${url.serviceContext}/enterprise/admin/indexcheck">Back</a>
  </body>
</html>