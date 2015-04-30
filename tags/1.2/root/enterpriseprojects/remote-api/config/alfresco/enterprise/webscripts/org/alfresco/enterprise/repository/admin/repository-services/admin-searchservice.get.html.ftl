<#include "../admin-template.ftl" />

<@page title=msg("searchservice.title") controller="/enterprise/admin/admin-searchservice">

<#assign hideError=false>

<#if searchAttributes?? && searchAttributes["sourceBeanName"]??>
   <div class="column-full">
      <p class="intro">${msg("searchservice.intro-text")?html}</p>
      <p class="info">${msg("searchservice.help-link")}</p>
      <@section label=msg("searchservice.searchservice") />
      <@attroptions id="searchService" attribute=searchAttributes["sourceBeanName"] label=msg("searchservice.searchservice.search-service-in-use") description=msg("searchservice.searchservice.search-service-in-use.description")>
         <@option label=msg("searchservice.searchservice.search-service-in-use.solr") value="solr" />
         <@option label=msg("searchservice.searchservice.search-service-in-use.solr4") value="solr4" />
         <@option label=msg("searchservice.searchservice.search-service-in-use.noindex") value="noindex" />
      </@attroptions>
   </div>
</#if>

<#if searchAttributes?? && solrAttributes?? && (solrAttributes?size > 1) && searchAttributes["sourceBeanName"]??>      
   <#if searchAttributes["sourceBeanName"].value == "solr">
       <#assign hideError=true>
   </#if>
   <div id="solrSearch" <#if searchAttributes["sourceBeanName"].value != "solr">class="hidden"</#if>>
      <div class="column-full">
         <@section label=msg("searchservice.solr-properties") />
      </div>
      <div class="column-left">
         <@attrcheckbox attribute=solrAttributes["search.solrTrackingSupport.enabled"] label=msg("searchservice.noindex-properties.search.solrTrackingSupport.enabled") description=msg("searchservice.noindex-properties.search.solrTrackingSupport.enabled.description") />
         <@attrtext attribute=solrAttributes["solr.port"] label=msg("searchservice.solr-properties.solr.port") description=msg("searchservice.solr-properties.solr.port.description") />
         <@attrtext attribute=solrAttributes["solr.baseUrl"] label=msg("searchservice.solr-properties.solr.baseUrl") description=msg("searchservice.solr-properties.solr.baseUrl.description") />
      </div>
      <div class="column-right">
         <@attrtext attribute=solrAttributes["solr.host"] label=msg("searchservice.solr-properties.solr.host") description=msg("searchservice.solr-properties.solr.host.description") />
         <@attrtext attribute=solrAttributes["solr.port.ssl"] label=msg("searchservice.solr-properties.solr.port.ssl") description=msg("searchservice.solr-properties.solr.port.ssl.description") />
      </div>

      <div class="column-full">
         <@section label=msg("searchservice.solr-properties.main-store-tracking-status") />
         <p class="info">${msg("searchservice.solr-properties.main-store-tracking-status.description")?html}</p>
         <#if solrAttributes["tracker.alfresco.active"].value = "true" || solrAttributes["tracker.alfresco.active"].value = "false">
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.active") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.active.description") value=msg("admin-console.yesno." + solrAttributes["tracker.alfresco.active"].value) />
         <#else>
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.active") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.active.description") value=msg("admin-console.unavailable") />
         </#if>
      </div>
      <div class="column-left">
         <#if solrAttributes["tracker.alfresco.last.indexed.txn"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.last.indexed.txn") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.last.indexed.txn.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.alfresco.last.indexed.txn"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.last.indexed.txn") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.last.indexed.txn.description") />
         </#if>
         <#if solrAttributes["tracker.alfresco.approx.indexing.time.remaining"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.indexing.time.remaining") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.indexing.time.remaining.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.alfresco.approx.indexing.time.remaining"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.indexing.time.remaining") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.indexing.time.remaining.description") />
         </#if>
         <#if solrAttributes["tracker.alfresco.disk"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.disk") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.disk.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.alfresco.disk"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.disk") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.disk.description") />
         </#if>
      </div>
      <div class="column-right">
         <#if solrAttributes["tracker.alfresco.lag"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.lag") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.lag.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.alfresco.lag"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.lag") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.lag.description") />
         </#if>
         <#if solrAttributes["tracker.alfresco.approx.txns.remaining"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.txns.remaining") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.txns.remaining.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.alfresco.approx.txns.remaining"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.txns.remaining") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.txns.remaining.description") />
         </#if>
         <#if solrAttributes["tracker.alfresco.memory"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.memory") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.memory.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.alfresco.memory"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.memory") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.memory.description") />
         </#if>
      </div>
      
      <div class="column-full">
         <@section label=msg("searchservice.solr-properties.archive-store-tracking-status") />
         <p class="info">${msg("searchservice.solr-properties.archive-store-tracking-status.description")?html}</p>
         <#if solrAttributes["tracker.archive.active"].value = "true" || solrAttributes["tracker.archive.active"].value = "false">
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.active") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.active.description") value=msg("admin-console.yesno." + solrAttributes["tracker.archive.active"].value) />
         <#else>
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.active") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.active.description") value=msg("admin-console.unavailable") />
         </#if>
      </div>
      <div class="column-left">
         <#if solrAttributes["tracker.archive.last.indexed.txn"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.last.indexed.txn") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.last.indexed.txn.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.archive.last.indexed.txn"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.last.indexed.txn") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.last.indexed.txn.description") />
         </#if>
         <#if solrAttributes["tracker.archive.approx.indexing.time.remaining"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.indexing.time.remaining") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.indexing.time.remaining.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.archive.approx.indexing.time.remaining"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.indexing.time.remaining") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.indexing.time.remaining.description") />
         </#if>
         <#if solrAttributes["tracker.archive.disk"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.disk") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.disk.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.archive.disk"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.disk") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.disk.description") />
         </#if>
      </div>
      <div class="column-right">
         <#if solrAttributes["tracker.archive.lag"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.lag") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.lag.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.archive.lag"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.lag") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.lag.description") />
         </#if>
         <#if solrAttributes["tracker.archive.approx.txns.remaining"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.txns.remaining") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.txns.remaining.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.archive.approx.txns.remaining"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.txns.remaining") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.txns.remaining.description") />
         </#if>
         <#if solrAttributes["tracker.archive.memory"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.memory") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.memory.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solrAttributes["tracker.archive.memory"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.memory") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.memory.description") />
         </#if>
      </div>
      
      <div class="column-full">
         <@section label=msg("searchservice.solr-properties.backup-settings") />
      </div>
      <div class="column-left">
         <@section label=msg("searchservice.solr-properties.backup-settings.main-store") />
         <@attrtext attribute=solrAttributes["solr.backup.alfresco.remoteBackupLocation"] label=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.remoteBackupLocation") description=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.remoteBackupLocation.description") />
         <@attrtext attribute=solrAttributes["solr.backup.alfresco.cronExpression"] label=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.cronExpression") description=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.cronExpression.description") />
         <@attrtext attribute=solrAttributes["solr.backup.alfresco.numberToKeep"] label=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.numberToKeep") description=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.numberToKeep.description") />
      </div>
      <div class="column-right">
         <@section label=msg("searchservice.solr-properties.backup-settings.archive-store-properties") />
         <@attrtext attribute=solrAttributes["solr.backup.archive.remoteBackupLocation"] label=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.remoteBackupLocation") description=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.remoteBackupLocation.description") />
         <@attrtext attribute=solrAttributes["solr.backup.archive.cronExpression"] label=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.cronExpression") description=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.cronExpression.description") />
         <@attrtext attribute=solrAttributes["solr.backup.archive.numberToKeep"] label=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.numberToKeep") description=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.numberToKeep.description") />
      </div>
      <@transactionQueryOptions mbean=solrAttributes />
  </div>
</#if>

<#if searchAttributes?? && solr4Attributes?? && (solr4Attributes?size > 1) && searchAttributes["sourceBeanName"]??> 
   <#if searchAttributes["sourceBeanName"].value == "solr4">
       <#assign hideError=true>
   </#if>
   <div id="solrSearch4" <#if searchAttributes["sourceBeanName"].value != "solr4">class="hidden"</#if>>
      <div class="column-full">
         <@section label=msg("searchservice.solr4-properties") />
      </div>
      <div class="column-left">
         <@attrcheckbox attribute=solr4Attributes["search.solrTrackingSupport.enabled"] label=msg("searchservice.noindex-properties.search.solrTrackingSupport.enabled") description=msg("searchservice.noindex-properties.search.solrTrackingSupport.enabled.description") />
         <@attrtext attribute=solr4Attributes["solr.port"] label=msg("searchservice.solr-properties.solr.port") description=msg("searchservice.solr-properties.solr.port.description") />
         <@attrtext attribute=solr4Attributes["solr.baseUrl"] label=msg("searchservice.solr-properties.solr.baseUrl") description=msg("searchservice.solr-properties.solr.baseUrl.description") />
      </div>
      <div class="column-right">
         <@attrtext attribute=solr4Attributes["solr.host"] label=msg("searchservice.solr-properties.solr.host") description=msg("searchservice.solr-properties.solr.host.description") />
         <@attrtext attribute=solr4Attributes["solr.port.ssl"] label=msg("searchservice.solr-properties.solr.port.ssl") description=msg("searchservice.solr-properties.solr.port.ssl.description") />
         <@attrcheckbox attribute=solr4Attributes["solr.suggester.enabled"] label=msg("searchservice.solr-properties.solr.suggester.enabled") description=msg("searchservice.solr-properties.solr.suggester.enabled.description") />
      </div>

      <div class="column-full">
         <@section label=msg("searchservice.solr-properties.main-store-tracking-status") />
         <p class="info">${msg("searchservice.solr-properties.main-store-tracking-status.description")?html}</p>
         <#if solr4Attributes["tracker.alfresco.active"].value = "true" || solr4Attributes["tracker.alfresco.active"].value = "false">
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.active") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.active.description") value=msg("admin-console.yesno." + solr4Attributes["tracker.archive.active"].value) />
         <#else>
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.active") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.active.description") value=msg("admin-console.unavailable") />
         </#if>
      </div>
      <div class="column-left">
         <#if solr4Attributes["tracker.alfresco.last.indexed.txn"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.last.indexed.txn") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.last.indexed.txn.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.alfresco.last.indexed.txn"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.last.indexed.txn") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.last.indexed.txn.description") />
         </#if>
         <#if solr4Attributes["tracker.alfresco.approx.indexing.time.remaining"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.indexing.time.remaining") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.indexing.time.remaining.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.alfresco.approx.indexing.time.remaining"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.indexing.time.remaining") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.indexing.time.remaining.description") />
         </#if>
         <#if solr4Attributes["tracker.alfresco.disk"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.disk") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.disk.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.alfresco.disk"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.disk") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.disk.description") />
         </#if>
      </div>
      <div class="column-right">
         <#if solr4Attributes["tracker.alfresco.lag"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.lag") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.lag.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.alfresco.lag"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.lag") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.lag.description") />
         </#if>
         <#if solr4Attributes["tracker.alfresco.approx.txns.remaining"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.txns.remaining") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.txns.remaining.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.alfresco.approx.txns.remaining"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.txns.remaining") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.approx.txns.remaining.description") />
         </#if>
         <#if solr4Attributes["tracker.alfresco.memory"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.memory") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.memory.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.alfresco.memory"] label=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.memory") description=msg("searchservice.solr-properties.main-store-tracking-status.tracker.alfresco.memory.description") />
         </#if>
      </div>
      
      <div class="column-full">
         <@section label=msg("searchservice.solr-properties.archive-store-tracking-status") />
         <p class="info">${msg("searchservice.solr-properties.archive-store-tracking-status.description")?html}</p>
         <#if solr4Attributes["tracker.archive.active"].value = "true" || solr4Attributes["tracker.archive.active"].value = "false">
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.active") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.active.description") value=msg("admin-console.yesno." + solr4Attributes["tracker.archive.active"].value) />
         <#else>
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.active") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.active.description") value=msg("admin-console.unavailable") />
         </#if>
      </div>
      <div class="column-left">
         <#if solr4Attributes["tracker.archive.last.indexed.txn"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.last.indexed.txn") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.last.indexed.txn.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.archive.last.indexed.txn"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.last.indexed.txn") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.last.indexed.txn.description") />
         </#if>
         <#if solr4Attributes["tracker.archive.approx.indexing.time.remaining"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.indexing.time.remaining") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.indexing.time.remaining.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.archive.approx.indexing.time.remaining"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.indexing.time.remaining") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.indexing.time.remaining.description") />
         </#if>
         <#if solr4Attributes["tracker.archive.disk"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.disk") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.disk.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.archive.disk"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.disk") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.disk.description") />
         </#if>
      </div>
      <div class="column-right">
         <#if solr4Attributes["tracker.archive.lag"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.lag") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.lag.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.archive.lag"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.lag") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.lag.description") />
         </#if>
         <#if solr4Attributes["tracker.archive.approx.txns.remaining"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.txns.remaining") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.txns.remaining.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.archive.approx.txns.remaining"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.txns.remaining") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.approx.txns.remaining.description") />
         </#if>
         <#if solr4Attributes["tracker.archive.memory"].value?starts_with("Unavailable: ") >
            <@field label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.memory") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.memory.description") value=msg("admin-console.unavailable") />
         <#else>
            <@attrfield attribute=solr4Attributes["tracker.archive.memory"] label=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.memory") description=msg("searchservice.solr-properties.archive-store-tracking-status.tracker.archive.memory.description") />
         </#if>
      </div>
      
      <div class="column-full">
         <@section label=msg("searchservice.solr-properties.backup-settings") />
      </div>
      <div class="column-left">
         <@section label=msg("searchservice.solr-properties.backup-settings.main-store") />
         <@attrtext attribute=solr4Attributes["solr.backup.alfresco.remoteBackupLocation"] label=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.remoteBackupLocation") description=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.remoteBackupLocation.description") />
         <@attrtext attribute=solr4Attributes["solr.backup.alfresco.cronExpression"] label=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.cronExpression") description=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.cronExpression.description") />
         <@attrtext attribute=solr4Attributes["solr.backup.alfresco.numberToKeep"] label=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.numberToKeep") description=msg("searchservice.solr-properties.backup-settings.main-store.solr.backup.alfresco.numberToKeep.description") />
      </div>
      <div class="column-right">
         <@section label=msg("searchservice.solr-properties.backup-settings.archive-store-properties") />
         <@attrtext attribute=solr4Attributes["solr.backup.archive.remoteBackupLocation"] label=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.remoteBackupLocation") description=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.remoteBackupLocation.description") />
         <@attrtext attribute=solr4Attributes["solr.backup.archive.cronExpression"] label=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.cronExpression") description=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.cronExpression.description") />
         <@attrtext attribute=solr4Attributes["solr.backup.archive.numberToKeep"] label=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.numberToKeep") description=msg("searchservice.solr-properties.backup-settings.archive-store-properties.solr.backup.archive.numberToKeep.description") />
      </div>
      <@transactionQueryOptions mbean=solr4Attributes />
  </div>
</#if>

<#if searchAttributes?? && noindexAttributes?? && (noindexAttributes?size >= 1) && searchAttributes["sourceBeanName"]??>
   <#if searchAttributes["sourceBeanName"].value == "noindex">
       <#assign hideError=true>
   </#if>
   <div id="noIndexSearch" <#if searchAttributes["sourceBeanName"].value != "noindex">class="hidden"</#if>>
      <div class="column-full">
         <@section label=msg("searchservice.noindex-properties") />
         <@attrcheckbox attribute=noindexAttributes["search.solrTrackingSupport.enabled"] label=msg("searchservice.noindex-properties.search.solrTrackingSupport.enabled") description=msg("searchservice.noindex-properties.search.solrTrackingSupport.enabled.description") />
      </div>
      <@transactionQueryOptions mbean=noindexAttributes />
   </div>
</#if>

<div id="errorMsg" <#if (hideError == true)>class="hidden"</#if>>
   <p class="error">${msg("searchservice.error-message")?html}</p>
</div>

   <script type="text/javascript">//<![CDATA[

/* Page load handler */
Admin.addEventListener(window, 'load', function() {
   var searchService = el("searchService");
   Admin.addEventListener(searchService, "change", function() {
         adminSS_searchOptionChanged(searchService)
      });
   adminSS_checkAllOptionsLoaded();
});

/**
 * Ensure that all search service options were loaded.
 * Otherwise hide all existing options and show error message.
  */
function adminSS_checkAllOptionsLoaded()
{
   var solrSearch = el("solrSearch");
   var solrSearch4 = el("solrSearch4");
   var noIndexSearch = el("noIndexSearch");
   var errorMsg = el("errorMsg");
   var optionElements = [solrSearch, solrSearch4, noIndexSearch];
   var total = optionElements.length;
   
   for (var i=0; i < total; i++)
   {
      if (!optionElements[i])
      {
         // some beans were not restarted
         // show error message
         Admin.removeClass(errorMsg, "hidden");
         // hide all other settings
         for (var j=0; j < total; j++)
         {
            var loadedEl = optionElements[j];
            if (loadedEl)
            {
               Admin.addClass(loadedEl, "hidden");
            }
         }
         // disable form
         var adminjmxform = el("admin-jmx-form");
         for (i = 0; i < adminjmxform.length; i++)
         {
            adminjmxform.elements[i].disabled = true;
         }
         // refresh after 30 sec
         setTimeout("location.reload(true);",30000);
         return;
      }
   }
   
}

/**
 * Set the correct bean attribute values for the different TLS options.
 * Each option requires its own attribute to be true and the others to be false
 * The values are set in hidden fields.
 * 
 * @param element The dropdown element that has changed
 */
function adminSS_searchOptionChanged(element)
{
   var solrSearch = el("solrSearch");
   var solrSearch4 = el("solrSearch4");
   var noIndexSearch = el("noIndexSearch");
   var searchService = element.options[element.selectedIndex].value;
   
   switch (searchService)
   {
      case "solr":      // Solr Search
         Admin.addClass(solrSearch4, "hidden");
         Admin.addClass(noIndexSearch, "hidden");
         Admin.removeClass(solrSearch, "hidden");
         break;
      case "solr4":      // Solr4 Search
         Admin.addClass(solrSearch, "hidden");
         Admin.addClass(noIndexSearch, "hidden");
         Admin.removeClass(solrSearch4, "hidden");
         break;
      case "noindex":   // No Index
         Admin.addClass(solrSearch4, "hidden");
         Admin.addClass(solrSearch, "hidden");
         Admin.removeClass(noIndexSearch, "hidden");
         break;
   }
}

//]]></script>

</@page>

<#macro transactionQueryOptions mbean>
      <div class="column-full">
         <@section label=msg("searchservice.transactional-query") />
         <p class="info">${msg("searchservice.transactional-query.description")?html}</p>
      </div>
      <div class="column-left">
         <#if patchAttributes["Applied"].value>
         <@attroptions attribute=mbean["solr.query.cmis.queryConsistency"] label=msg("searchservice.transactional-query.cmis")>
            <@option label=msg("searchservice.transactional-query.eventual") value="EVENTUAL" />
            <@option label=msg("searchservice.transactional-query.transactional") value="TRANSACTIONAL" />
            <@option label=msg("searchservice.transactional-query.transactional-if-possible") value="TRANSACTIONAL_IF_POSSIBLE" />
            <@option label=msg("searchservice.transactional-query.hybrid") value="HYBRID" />
         </@attroptions>
         <#else>
         <div class="control options">
            <span class="label">${msg("searchservice.transactional-query.cmis")?html}:</span>
            <span class="value">
               <select tabindex="0" disabled="true">
                  <option selected="selected" value="EVENTUAL">${msg("searchservice.transactional-query.eventual")?html}</option>
                  <option value="TRANSACTIONAL">${msg("searchservice.transactional-query.transactional")?html}</option>
                  <option value="TRANSACTIONAL_IF_POSSIBLE">${msg("searchservice.transactional-query.transactional-if-possible")?html}</option>
                  <option value="HYBRID">${msg("searchservice.transactional-query.hybrid")?html}</option>
               </select>
            </span>
         </div>
         </#if>
      </div>
      <div class+"column-right">
         <#if patchAttributes["Applied"].value>
         <@attroptions attribute=mbean["solr.query.fts.queryConsistency"] label=msg("searchservice.transactional-query.fts")>
            <@option label=msg("searchservice.transactional-query.eventual") value="EVENTUAL" />
            <@option label=msg("searchservice.transactional-query.transactional") value="TRANSACTIONAL" />
            <@option label=msg("searchservice.transactional-query.transactional-if-possible") value="TRANSACTIONAL_IF_POSSIBLE" />
            <@option label=msg("searchservice.transactional-query.hybrid") value="HYBRID" />
         </@attroptions>
         <#else>
         <div class="control options">
            <span class="label">${msg("searchservice.transactional-query.cmis")?html}:</span>
            <span class="value">
               <select tabindex="0" disabled="true">
                  <option selected="selected" value="EVENTUAL">${msg("searchservice.transactional-query.eventual")?html}</option>
                  <option value="TRANSACTIONAL">${msg("searchservice.transactional-query.transactional")?html}</option>
                  <option value="TRANSACTIONAL_IF_POSSIBLE">${msg("searchservice.transactional-query.transactional-if-possible")?html}</option>
                  <option value="HYBRID">${msg("searchservice.transactional-query.hybrid")?html}</option>
               </select>
            </span>
         </div>
         </#if>
      </div> 
      <div class="column-full">
         <p class="light">${msg("searchservice.transactional-query.eventual.description")?html}</p>
         <p class="light">${msg("searchservice.transactional-query.transactional.description")?html}</p>
         <p class="light">${msg("searchservice.transactional-query.transactional-if-possible.description")?html}</p>
         <p class="light">${msg("searchservice.transactional-query.hybrid.description")?html}</p>
         <p class="info">${msg("searchservice.transactional-query.info-text")}</p>
      </div>
</#macro>
