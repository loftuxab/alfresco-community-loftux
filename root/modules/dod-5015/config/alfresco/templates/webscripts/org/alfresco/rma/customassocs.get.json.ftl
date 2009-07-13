<#escape x as jsonUtils.encodeJSONString(x)>
{
   data :
   {
      customAssociations :
      {
         <#list customAssocs as assoc>
         "${assoc.name}" :
         {
            "isChildAssociation" : ${assoc.childAssociation?string},
            <#if assoc.type??>"type" : "${assoc.type}",</#if>
            <#if assoc.title??>"title" : "${assoc.title}",</#if>
            <#if assoc.description??>"description" : "${assoc.description}",</#if>
            <#if assoc.sourceRoleName??>"sourceRoleName" : "${assoc.sourceRoleName}",</#if>
            <#if assoc.sourceMandatory??>"sourceMandatory" : ${assoc.sourceMandatory?string},</#if>
            <#if assoc.sourceMany??>"sourceMany" : ${assoc.sourceMany?string},</#if>
            <#if assoc.targetRoleName??>"targetRoleName" : "${assoc.targetRoleName}",</#if>
            <#if assoc.targetMandatory??>"targetMandatory" : ${assoc.targetMandatory?string},</#if>
            <#if assoc.targetMandatoryEnforced??>"targetMandatoryEnforced" : ${assoc.targetMandatoryEnforced?string},</#if>
            <#if assoc.targetMany??>"targetMany" : ${assoc.targetMany?string},</#if>
            <#if assoc.protected??>"protected" : ${assoc.protected?string},</#if>
         }<#if assoc_has_next>,</#if>
         </#list>
      }
   }
}
</#escape>
