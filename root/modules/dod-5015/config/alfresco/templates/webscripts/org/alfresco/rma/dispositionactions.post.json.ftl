{
   "data":
   {
      "items":
      [
      <#list results as row>
         {
            "action": "${row.item.properties["rma:dispositionAction"]!""}",
            "description": "${row.item.properties["rma:dispositionDescription"]!""}",
            "period": "${row.item.properties["rma:dispositionPeriod"]!""}",
            "periodproperty": "${row.item.properties["rma:dispositionPeriodProperty"]!""}",
            "event": "${row.item.properties["rma:dispositionEvent"]!""}",
            "trigger": "${row.item.properties["rma:dispositionEventTrigger"]!""}",
            "nodeRef": "${row.item.nodeRef}"
         }<#if row_has_next>,</#if>
      </#list>
      ]
   }
}