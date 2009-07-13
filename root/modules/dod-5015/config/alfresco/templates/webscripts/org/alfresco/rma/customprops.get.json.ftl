<#escape x as jsonUtils.encodeJSONString(x)>
{
   data :
   {
      customProperties :
      {
         <#list customProps as prop>
         "${prop.name}" :
         {
            <#if prop.type??>"type" : "${prop.type}",</#if>
            <#if prop.title??>"title" : "${prop.title}",</#if>
            <#if prop.description??>"description" : "${prop.description}",</#if>
            <#if prop.mandatory??>"mandatory" : ${prop.mandatory?string},</#if>
            <#if prop.multiValued??>"multiValued" : ${prop.multiValued?string},</#if>
            <#if prop.defaultValue??>"defaultValue" : "${prop.defaultValue}",</#if>
            <#if prop.protected??>"protected" : ${prop.protected?string}</#if>
         }<#if prop_has_next>,</#if>
         </#list>
      }
   }
}
</#escape>
