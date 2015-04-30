<#include "admin-template.ftl" />

<@page title=msg("example.title")>
   
   <div class="column-full">
      <@section label=msg("example.column") />
      <#-- Example - Retrieve keys - which are attribute names - use to index into attribute hash -->
      <#-- You can index directly by attribute name e.g. <@control attribute=attributes["Subject"] /> -->
      <#list attributes?keys as a>
         <@control attribute=attributes[a] />
      </#list>
   </div>
   
   <div class="column-left">
      <@section label=msg("example.leftcolumn") />
      <#-- Example - Retrieve values - which are attributes -->
      <#list attributes?values as a>
         <@control attribute=a />
      </#list>
   </div>
   <div class="column-right">
      <@section label=msg("example.rightcolumn") />
      <#-- Example - Retrieve values - which are attributes -->
      <#list attributes?values as a>
         <@control attribute=a />
      </#list>
   </div>
   
</@page>