<#-- Renders a SyncSetDefinition object. -->
<#macro renderSsd ssd>
<#escape x as jsonUtils.encodeJSONString(x)>
   {
      "id": "${ssd.id}",
      "nodeRef": "${ssd.nodeRef?string}",
      "sourceCopyLocked": ${ssd.lockSourceCopy?string},
      "includeSubFolders": ${ssd.includeSubFolders?string},
      "remoteTenantId": "${ssd.remoteTenantId}",
      "remoteTargetFolderNodeRef": "${ssd.targetFolderNodeRef}",
      "isDeleteOnPrem": ${ssd.deleteOnPremFlag?string},
      "isDeleteOnCloud" : ${ssd.deleteOnCloudFlag?string}
      
   }
</#escape>
</#macro>
<#macro renderCloudSsd ssd>
<#escape x as jsonUtils.encodeJSONString(x)>
   {
      "id": "${ssd.id}",
      "nodeRef": "${ssd.nodeRef?string}"
   }
</#escape>
</#macro>
