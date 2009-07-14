{
   "data" :
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
   }
}