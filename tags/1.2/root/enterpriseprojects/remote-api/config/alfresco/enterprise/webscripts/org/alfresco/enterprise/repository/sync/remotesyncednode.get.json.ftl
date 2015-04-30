<#escape x as jsonUtils.encodeJSONString(x)>
   {
      "remoteNetworkId": "${remoteNetworkId!""}"
      <#if remoteNodeRef??>
      ,
      "remoteNodeRef": "${remoteNodeRef}"
      </#if>
      <#if remoteParentNodeRef??>
      ,
      "remoteParentNodeRef": "${remoteParentNodeRef}"
      </#if>
      <#if localRootNodeRef??>
      ,
      "localRootNodeRef": "${localRootNodeRef}"
      </#if>
      <#if localRootNodeName??>
      ,
      "localRootNodeName": "${localRootNodeName}"
      </#if>
      <#if syncSetOwnerFirstName??>
      ,
      "syncSetOwnerFirstName": "${syncSetOwnerFirstName}"
      </#if>
      <#if syncSetOwnerFirstName??>
      ,
      "syncSetOwnerLastName": "${syncSetOwnerLastName}"
      </#if>
      <#if syncSetOwnerUserName??>
      ,
      "syncSetOwnerUserName": "${syncSetOwnerUserName}"
      </#if>
   }
</#escape>
