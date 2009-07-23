<#import "customassociation.lib.ftl" as customAssociationLib/>

<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data" :
   {
      "nodeRef" : "${nodeRef}",
      "customSourceAssociations" :
      [
          <#list sourceassocs as assoc>
             {
             "assocTypeQName" : "${assoc.assocTypeQName}",
             "sourceNodeRef" : "${assoc.sourceRef}",
             "targetNodeRef" : "${assoc.targetRef}",
             <@customAssociationLib.customAssociationJSON association=assoc.associationDefinition/>
             }
             <#if assoc_has_next>,</#if>
          </#list>
      ],
      "customTargetAssociations" :
      [
          <#list targetassocs as assoc>
             {
             "assocTypeQName" : "${assoc.assocTypeQName}",
             "sourceNodeRef" : "${assoc.sourceRef}",
             "targetNodeRef" : "${assoc.targetRef}",
             <@customAssociationLib.customAssociationJSON association=assoc.associationDefinition/>
             }
             <#if assoc_has_next>,</#if>
          </#list>
      ]
   }
}
</#escape>
