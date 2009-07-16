{
   "data" :
   {
      "url" : "${nextaction.url}",
      "name" : "${nextaction.name}",
      "label" : "${nextaction.label}",
      "eventsEligible" : ${nextaction.eventsEligible?string},
      <#if nextaction.asOf??>"asOf" : "${nextaction.asOf}",</#if>
      <#if nextaction.startedAt??>"startedAt" : "${nextaction.startedAt}",</#if>
      <#if nextaction.startedBy??>"startedBy" : "${nextaction.startedBy}",</#if>
      <#if nextaction.completedAt??>"completedAt" : "${nextaction.completedAt}",</#if>
      <#if nextaction.completedBy??>"completedBy" : "${nextaction.completedBy}",</#if>
      "events" : 
      [
         <#list nextaction.events as event>
         {
            "name" : "${event.name}",
            "label" : "${event.label}",
            "complete" : ${event.complete?string},
            <#if event.completedAt??>"completedAt" : "${event.completedAt}",</#if>
            <#if event.completedBy??>"completedBy" : "${event.completedBy}",</#if>
            "automatic" : ${event.automatic?string}
         }<#if event_has_next>,</#if>
         </#list>
      ]
   }
}