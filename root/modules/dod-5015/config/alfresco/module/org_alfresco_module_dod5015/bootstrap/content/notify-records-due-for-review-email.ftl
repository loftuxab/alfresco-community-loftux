Alfresco Records Management

Records due for review notification.

<#list records as record>   
     ${record.name}        
     <#if record_has_next>
     </#if>
</#list>

