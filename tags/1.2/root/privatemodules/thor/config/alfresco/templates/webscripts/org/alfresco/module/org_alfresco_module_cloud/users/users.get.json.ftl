<#import "users.lib.ftl" as usersLib/>
<#import "../../../repository/generic-paged-results.lib.ftl" as genericPaging />
{
   "data": 
   [
      <#list peopleList as person>
         <@usersLib.personJSON person=person/>
         <#if person_has_next>,</#if>
      </#list>
   ]

   <@genericPaging.pagingJSON pagingVar="paging" />
}
