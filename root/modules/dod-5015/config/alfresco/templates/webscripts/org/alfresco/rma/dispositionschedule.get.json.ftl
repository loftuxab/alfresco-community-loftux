{
   "data" :
   {
      "url" : "${schedule.url}",
      "authority" : "${schedule.authority}",
      "instructions" : "${schedule.instructions}",
      "recordLevelDisposition" : ${schedule.recordLevelDisposition?string},
      "actionsUrl" : "${schedule.actionsUrl}",
      "actions" : 
      [
         <#list schedule.actions as action>
         {
            "id" : "${action.id}",
            "url" : "${action.url}",
            "index" : ${action.index},
            "name" : "${action.name}",
            <#if action.description??>
            "description" : "${action.description}",
            </#if>
            <#if action.period??>
            "period" : "${action.period}",
            </#if>
            <#if action.periodProperty??>
            "periodProperty" : "${action.periodProperty}",
            </#if>
            <#if action.events??>
            "events" : [<#list action.events as event>${event}<#if event_has_next>,</#if></#list>],
            </#if>
            "eligibleOnFirstCompleteEvent" : ${action.eligibleOnFirstCompleteEvent?string}
         }<#if action_has_next>,</#if>
         </#list>
      ]
   }
}