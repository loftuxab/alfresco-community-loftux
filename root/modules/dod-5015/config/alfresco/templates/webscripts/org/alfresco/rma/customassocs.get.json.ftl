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
            "title" : <#if assoc.title??>"${assoc.title}"<#else>""</#if>,
            "description" : <#if assoc.description??>"${assoc.description}"<#else>""</#if>,
            "sourceRoleName" : <#if assoc.sourceRoleName??>"${assoc.sourceRoleName}"<#else>""</#if>,
            "sourceMandatory" : <#if assoc.sourceMandatory??>${assoc.sourceMandatory?string}<#else>""</#if>,
            "sourceMany" : <#if assoc.sourceMany??>${assoc.sourceMany?string}<#else>""</#if>,
            "targetRoleName" : <#if assoc.targetRoleName??>"${assoc.targetRoleName}"<#else>""</#if>,
            "targetMandatory" : <#if assoc.targetMandatory??>${assoc.targetMandatory?string}<#else>""</#if>,
            "targetMandatoryEnforced" : <#if assoc.targetMandatoryEnforced??>${assoc.targetMandatoryEnforced?string}<#else>""</#if>,
            "targetMany" : <#if assoc.targetMany??>${assoc.targetMany?string}<#else>""</#if>,
            "protected" : <#if assoc.protected??>${assoc.protected?string}<#else>""</#if>,
         }<#if assoc_has_next>,</#if>
         </#list>
      }
   }
}
</#escape>