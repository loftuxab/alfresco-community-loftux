<#include "admin-template.ftl" />

<@page title=msg("admin-console.tool.admin-systemsummary.label") readonly=true>
   
   <@section label=msg("systemsummary.system-information") />
   <div class="column-left">
      <@attrfield attribute=sysPropsAttributes["alfresco.home"] label=msg("systemsummary.system-information.alfresco-home") />
      <@attrfield attribute=alfrescoAttributes["Edition"] label=msg("systemsummary.system-information.alfresco-edition") />
      <@attrfield attribute=alfrescoAttributes["VersionNumber"] label=msg("systemsummary.system-information.alfresco-version") />
      <@attrfield attribute=sysPropsAttributes["java.home"] label=msg("systemsummary.system-information.java-home") />
      <@attrfield attribute=sysPropsAttributes["java.version"] label=msg("systemsummary.system-information.java-version") />
      <@attrfield attribute=sysPropsAttributes["java.vm.vendor"] label=msg("systemsummary.system-information.java-vm-vendor") />
   </div>

   <div class="column-right">
      <@attrfield attribute=sysPropsAttributes["os.name"] label=msg("systemsummary.system-information.operating-system") />
      <@attrfield attribute=sysPropsAttributes["os.version"] label=msg("systemsummary.system-information.version") />
      <@attrfield attribute=sysPropsAttributes["os.arch"] label=msg("systemsummary.system-information.architecture") />
      <@attrfield attribute=memoryAttributes["FreeMemory"] label=msg("systemsummary.system-information.free-memory") />
      <@attrfield attribute=memoryAttributes["MaxMemory"] label=msg("systemsummary.system-information.maximum-memory") />
      <@attrfield attribute=memoryAttributes["TotalMemory"] label=msg("systemsummary.system-information.total-memory") />
      <@attrfield attribute=memoryAttributes["AvailableProcessors"] label=msg("systemsummary.system-information.cpus") />
   </div>
   
   <div class="column-full"></div>
   
   <div class="column-left">
      <@section label=msg("systemsummary.file-systems") />
      <@attrstatus attribute=fileSystemsAttributes["cifs.enabled"] label=msg("systemsummary.file-systems.cifs-enabled") />
      <@attrstatus attribute=fileSystemsAttributes["ftp.enabled"] label=msg("systemsummary.file-systems.ftp-enabled") />
      <@attrstatus attribute=fileSystemsAttributes["nfs.enabled"] label=msg("systemsummary.file-systems.nfs-enabled") />
      <@attrstatus attribute=webdavAttributes["Enabled"] label=msg("systemsummary.file-systems.webdav-enabled") />
      <@section label=msg("systemsummary.transformation-services") />
      <@attrstatus attribute=ooDirectAttributes["ooo.enabled"] label=msg("systemsummary.transformation-services.openoffice-direct-enabled") />
      <@attrstatus attribute=jodConvAttributes["jodconverter.enabled"] label=msg("systemsummary.transformation-services.jod-converster-enabled") />
      <@attrstatus attribute=swfToolsAttributes["Available"] label=msg("systemsummary.transformation-services.swf-tools") />
      <@attrstatus attribute=imageMagicAttributes["Available"] label=msg("systemsummary.transformation-services.imagemagic") />
      <#if fFMpegAttributes["Available"]?has_content>
         <@attrstatus attribute=fFMpegAttributes["Available"] label=msg("systemsummary.transformation-services.ffmpeg") />
      <#else>
         <@field label=msg("systemsummary.transformation-services.ffmpeg") value=msg("admin-console.not.installed") />
      </#if>

      <@section label=msg("systemsummary.indexing-subsystem") />
      <#if indexingAttributes["sourceBeanName"].value = "solr"><#assign solr="true"><#else><#assign solr="false"></#if>
      <@status label=msg("systemsummary.indexing-subsystem.solr") value=solr />
      <#if indexingAttributes["sourceBeanName"].value = "solr4"><#assign solr4="true"><#else><#assign solr4="false"></#if>
      <@status label=msg("systemsummary.indexing-subsystem.solr4") value=solr4 />
      <#if indexingAttributes["sourceBeanName"].value = "noindex"><#assign noindex="true"><#else><#assign noindex="false"></#if>
      <@status label=msg("systemsummary.indexing-subsystem.noindex") value=noindex />

      <@section label=msg("systemsummary.repository-clustering") />
      <@attrstatus attribute=clusteringAttributes["ClusteringEnabled"] label=msg("systemsummary.repository-clustering.enabled") />
      <@attrfield attribute=clusteringAttributes["ClusterName"] label=msg("systemsummary.repository-clustering.cluster-name") />
      <@attrfield attribute=clusteringAttributes["NumClusterMembers"] label=msg("systemsummary.repository-clustering.cluster-members") />

      <@section label=msg("systemsummary.activities-feed") />
      <@attrstatus attribute=activitesAttributes["activities.feed.notifier.enabled"] label=msg("systemsummary.activities-feed.enabled") />

      <@section label=msg("systemsummary.authentication") />
      <p class="label">${msg("systemsummary.authentication.authentication-directories")?html}:</p>
      <table class="data">
         <tr>
            <th></th>
            <th>${msg("systemsummary.authentication.name")?html}</th>
            <th>${msg("systemsummary.authentication.type")?html}</th>
         </tr>
      <#list authenticationDirectories as directory>
         <tr>
            <td>${directory_index + 1}.</td>
            <td>${urldecode(directory.name)?html}</td>
            <td>${(directory.type)?html}</td>
         </tr>
      </#list>
      </table>
      
      <#if synchronizedDirectories?has_content>
      <p class="label">${msg("systemsummary.authentication.syncronization-directories")?html}:</p>
      <table class="data">
         <tr>
            <th></th>
            <th>${msg("systemsummary.authentication.name")?html}</th>
            <th>${msg("systemsummary.authentication.type")?html}</th>
         </tr>
      <#list synchronizedDirectories as directory>
         <tr>
            <td>${directory_index + 1}.</td>
            <td>${urldecode(directory.name)?html}</td>
            <td>${(directory.type)?html}</td>
         </tr>
      </#list>
      </table>
      </#if>
   </div>

   <div class="column-right">
      <@section label=msg("systemsummary.email") />
      <#if inEmailAttributes["email.inbound.enabled"].value = "true" && inEmailAttributes["email.server.enabled"].value = "true">
         <@status label=msg("systemsummary.email.inbound-enabled") value=cvalue(inEmailAttributes["email.inbound.enabled"].type, "true") />
      <#else>
         <@status label=msg("systemsummary.email.inbound-enabled") value=cvalue(inEmailAttributes["email.inbound.enabled"].type, "false") />
      </#if>
      <@attrstatus attribute=imapEmailAttributes["imap.server.enabled"] label=msg("systemsummary.email.imap-enabled") />

      <@section label=msg("systemsummary.auditing-services") />
      <@attrstatus attribute=auditingAttributes["audit.enabled"] label=msg("systemsummary.auditing-services.audit") />
      <@attrstatus attribute=auditingAttributes["audit.cmischangelog.enabled"] label=msg("systemsummary.auditing-services.cmis-change-log") />
      <@attrstatus attribute=auditingAttributes["audit.alfresco-access.enabled"] label=msg("systemsummary.auditing-services.alfresco-access") />
      <@attrstatus attribute=auditingAttributes["audit.tagging.enabled"] label=msg("systemsummary.auditing-services.tagging") />
      <@attrstatus attribute=auditingAttributes["audit.sync.enabled"] label=msg("systemsummary.auditing-services.sync") />

      <@section label=msg("systemsummary.content-stores") />
      <#list contentStoreAttributes as attribute>
         <@attrfield attribute=attribute["StorePath"] label=msg("systemsummary.content-stores.store-path")>
            <@attrfield attribute=attribute["SpaceUsed"] label=msg("systemsummary.content-stores.space-used") style="margin-left: 1em; padding-bottom: 0;" />
            <@attrfield attribute=attribute["SpaceFree"] label=msg("systemsummary.content-stores.space-available") style="margin-left: 1em; padding-bottom: 0;" />
         </@attrfield>
      </#list>
      
      <@section label=msg("systemsummary.amps") />
      <@field label=msg("systemsummary.amps.currently-installed") >
         <ul style="margin-left: 1em;">
         <#list installedAMPs as current>
         <#if current["module.version"]?has_content>
            <li>${msg("systemsummary.amps.amp-name",current["module.id"],current["module.version"])?html}</li>
         <#else>
            <li>${current["module.id"]?html}</li>
         </#if>
         </#list>
         </ul>
      </@field>
      <@field label=msg("systemsummary.amps.previously-installed") >
         <ul style="margin-left: 1em;">
         <#list previousAMPs as current>
         <#if current["module.version"]?has_content>
            <li>${msg("systemsummary.amps.amp-name",current["module.id"],current["module.version"])?html}</li>
         <#else>
            <li>${current["module.id"]?html}</li>
         </#if>
         </#list>
         </ul>
      </@field>

      <@section label=msg("systemsummary.users-groups") />
      <@attrfield attribute=authorityAttributes["NumberOfUsers"] label=msg("systemsummary.users-groups.users") />
      <@attrfield attribute=authorityAttributes["NumberOfGroups"] label=msg("systemsummary.users-groups.groups") />
   </div>   
   
</@page>