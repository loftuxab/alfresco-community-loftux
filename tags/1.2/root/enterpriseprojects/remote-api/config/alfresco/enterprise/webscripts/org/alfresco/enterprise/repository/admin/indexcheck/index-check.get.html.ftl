<#import "/org/alfresco/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <@wsLib.head>Alfresco DM Index Check (running on: ${ipAddress})</@wsLib.head>
  <body>
    <div>
    <@wsLib.header>Alfresco DM Index/Txn Consistency Check (running on: <b>${ipAddress}</b>)</@wsLib.header>
    <br/>
    
    <span class="mainSubTitle">WARNING</span>
    <table>
        <tr><td><ul><li>This interface should only be used with guidance from <a href="http://support.alfresco.com">Alfresco Support</a></li></ul></td></tr>
    </table>    
    
    <span class="mainSubTitle">Index Txns Check</span>
    <table>
        
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/progress" method="get" target="_blank">
        <tr><td><ul><li>Get index check progress / last report run:<input type="submit" value="Get Progress"></li></ul></td></tr>
        </form>
        
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/checktxn" method="get" target="_blank">
        <tr><td><ul><li>Check last txn id :<input type="submit" value="Check Last"></li></ul></td></tr>
        </form>
        
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/checktxn" method="get" target="_blank">
        <tr><td><ul><li>Check specified txn id (eg. 123456789) :<input name="txnId" size="20" value=""><input type="submit" value="Check One"></li></ul></td></tr>
        </form>
        
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/checktxns" method="get" target="_blank">
        <tr><td><ul><li>Check txn ids from/to specified txn ids (eg. 123456789) :<input name="fromTxnId" size="20" value=""><input name="toTxnId" size="20" value=""><input type="submit" value="Check From/To !!">(warning: this may take some time)</li></ul></td></tr>
        </form>
        
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/checktxns" method="get" target="_blank">
        <tr><td><ul><li>Check txn ids from specified txn id (eg. 123456789) :<input name="fromTxnId" size="20" value=""><input type="submit" value="Check From !!">(warning: this may take some time)</li></ul></td></tr>
        </form>
        
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/checktxns" method="get" target="_blank">
        <tr><td><ul><li>Check txn ids from/to specified times (eg. 2009-01-26T14:36:32.502Z) :<input name="fromTime" size="20" value=""><input name="toTime" size="20" value=""><input type="submit" value="Check From/To !!">(warning: this may take some time)</li></ul></td></tr>
        </form>
        
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/checktxns" method="get" target="_blank">
        <tr><td><ul><li>Check txn ids from specified time (eg. 2009-01-26T14:36:32.502Z) :<input name="fromTime" size="20" value=""><input type="submit" value="Check From !!">(warning: this may take some time)</li></ul></td></tr>
        </form>
        
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/checktxns/all" method="get" target="_blank">
        <tr><td><ul><li>Check all txn ids: <input type="submit" value="Check All !!">(warning: this may take some time)</li></ul></td></tr>
        </form>
        
    </table>
    
    <span class="mainSubTitle">Index Nodes Check</span>
    <table>
    
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/checknodes" method="get" target="_blank">
        <tr><td><ul><li>Check node for specified nodeRef (eg. workspace://SpacesStore/....) :<input name="nodeRef" size="20" value=""><input type="submit" value="Check Node"></li></ul></td></tr>
        </form>
        
        <form action="${url.serviceContext}/enterprise/admin/indexcheck/adm/checknodes" method="get" target="_blank">
        <tr><td><ul><li>Check nodes for specified txn id (eg. 123456789) :<input name="txnId" size="20" value=""><input type="submit" value="Check Txn Nodes"></li></ul></td></tr>
        </form>
        
    </table>
           
    <span class="mainSubTitle">Re-index Txns</span>
    <table>
        
        <form action="${url.serviceContext}/enterprise/admin/reindex/adm/progress" method="get" target="_blank">
        <tr><td><ul><li>Get re-index/tracker progress :<input type="submit" value="Get Progress"></li></ul></td></tr>
        </form>
		
        <form action="${url.serviceContext}/enterprise/admin/reindex/adm/from" method="post" target="_blank">
        <tr><td><ul><li>Re-index from specified txn id (eg. 123456789) :<input name="fromTxnId" size="20" value=""><input type="submit" value="Re-index From !!">(note: will run in background)</li></ul></td></tr>
        </form>
        
        <form action="${url.serviceContext}/enterprise/admin/reindex/adm/from" method="post" target="_blank">
        <tr><td><ul><li>Re-index from specified time (eg. 2009-01-26T14:36:32.502Z) :<input name="fromTime" size="20" value=""><input type="submit" value="Re-index From !!">(note: will run in background)</li></ul></td></tr>
        <tr><td></td></tr>
        </form>
        
    </table>
    <br/>
  </body>    
</html>