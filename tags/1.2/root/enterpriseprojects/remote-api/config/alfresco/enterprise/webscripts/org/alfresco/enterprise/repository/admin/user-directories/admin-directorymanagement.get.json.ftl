<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data": [
   <#list auths as a>
      {
         "id": "${a.id}",
         "name": "${a.name}",
         "type": "${a.type}",
         "active": ${a.active?string},
         "synched": ${a.synched?string},
         "cifsSelected": ${a.cifsSelected?string},
         "browserSelected": ${a.browserSelected?string},
         "syncStatus": [
         <#list a.syncStatus as s>
            {
               "id": "${s.id}",
               "startTime": "<#if s.startTime??>${s.startTime?string("dd MMM yyyy HH:mm:ss")}</#if>",
               "endTime": "<#if s.endTime??>${s.endTime?string("dd MMM yyyy HH:mm:ss")}</#if>",
               "percentComplete": "${s.percentComplete}",
               "totalResults": "${s.totalResults}",
               "totalErrors": "${s.totalErrors}",
               "successfullyProcessedEntries": "${s.successfullyProcessedEntries}"
            }<#if s_has_next>,</#if>
         </#list>
         ]
      }<#if a_has_next>,</#if>
   </#list>
   ],
   "syncStatus": "${syncStatus}"
}
</#escape>