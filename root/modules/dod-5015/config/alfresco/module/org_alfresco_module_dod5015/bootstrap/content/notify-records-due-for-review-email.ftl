Alfresco Records Management

Records due for review notification.

<#list records as record>   
     ${record.identifier!} ${record.name!}<#if record_has_next>,
     </#if>
</#list>

