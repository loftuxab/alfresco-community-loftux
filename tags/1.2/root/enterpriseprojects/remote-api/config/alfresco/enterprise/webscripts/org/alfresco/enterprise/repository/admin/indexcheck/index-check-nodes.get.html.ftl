<#import "/org/alfresco/webscripts.lib.html.ftl" as wsLib/>
<#import "index-check.lib.html.ftl" as icLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Alfresco DM Index Check - check nodes (running on: ${ipAddress})</@wsLib.head>
  <body>
    <div>
    <@wsLib.header>Alfresco DM Index/Txn Consistency Check - check nodes (running on: <b>${ipAddress}</b>)</@wsLib.header>
    <br/>
    <p/>
    <@icLib.reportDetails></@icLib.reportDetails>
    <p/>
    <#if indexTxnInfo.nodeList ??>
    <span class="mainSubTitle">Txn Node List</span>
    <table>
        <#list indexTxnInfo.nodeList as nodeInfo>
        <tr><td>${nodeInfo}</td></tr>
        </#list>
    </table>
    </#if>
    <p/>
    <a href="${url.serviceContext}/enterprise/admin/indexcheck">Back</a>
  </body>
</html>