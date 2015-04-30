[#ftl]

[#macro reportDetails]
    
    <span class="mainSubTitle">Report Run Details</span>
    <table>
        <tr>  <td>Report Run Title</td>     <td>${indexTxnInfo.title}</td></tr>
        <tr>  <td>Report Run Server</td>    <td>${indexTxnInfo.ipAddress}</td></tr>
        
        <tr></tr>
        <tr>  <td>Report Run Started</td>   <td>${xmldate(indexTxnInfo.runStartTime)}</td></tr>
        <tr>  <td>Report Run Ended</td>     <td>${xmldate(indexTxnInfo.runEndTime)}</td></tr>
        <tr></tr>
                
        [#if indexTxnInfo.missingCount gt 0]
        <tr>  <td>Txn Out-Of-Sync Count</td>     <td>${indexTxnInfo.missingCount?c}</td></tr>
        <tr>  <td>First Out-Of-Sync Txn Id</td>  <td>${indexTxnInfo.firstMissingTxn}</td></tr>
        <tr>  <td>Last Out-Of-Sync Txn Id</td>   <td>${indexTxnInfo.lastMissingTxn}</td></tr>
        <tr></tr>
        [/#if]

        <tr>  <td>Txn Processed Count</td>   <td>${indexTxnInfo.processedCount?c}</td></tr>
        <tr>  <td>First Processed Txn Id</td>  <td>${indexTxnInfo.firstProcessedTxn}</td></tr>
        <tr>  <td>Last Processed Txn Id</td>   <td>${indexTxnInfo.lastProcessedTxn}</td></tr>
        <tr></tr>

        <tr>  <td>Min Txn Time (all txns)</td>   <td>${xmldate(indexTxnInfo.minTxnTime)}</td></tr>
        <tr>  <td>Max Txn Time (all txns)</td>   <td>${xmldate(indexTxnInfo.maxTxnTime)}</td></tr>
    </table>
    
[/#macro]