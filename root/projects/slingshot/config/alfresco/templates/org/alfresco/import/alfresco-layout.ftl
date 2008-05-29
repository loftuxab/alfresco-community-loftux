<#macro grid noOfColumns columnFlow numberOfComponents regionIdPrefix>
   <#if (columnFlow == "fluid" && noOfColumns == 1)>
      <@_fluidGrid noOfColumns numberOfComponents regionIdPrefix ""/>
   <#elseif (columnFlow == "fluid" && noOfColumns == 2)>
      <@_fluidGrid noOfColumns numberOfComponents regionIdPrefix "yui-g"/>
   <#elseif (columnFlow == "fluid" && noOfColumns == 3)>
      <@_fluidGrid noOfColumns numberOfComponents regionIdPrefix "yui-gb"/>
   <#elseif (columnFlow == "fluid" && noOfColumns > 3)>
      Error:: alfresco-layout.ftl@grid :: Can only handle 1,2 or 3 columns
   </#if>
</#macro>

<#macro _fluidGrid numberOfColumns numberOfComponents regionIdPrefix nestingGridClass>
   <div class="${nestingGridClass}">
   <#list 1..numberOfColumns as column>
      <div class="yui-u<#if column == 1> first</#if>">
      <#list 1..numberOfComponents as component>
         <#if (component % numberOfColumns) == (column % numberOfColumns)>
         <@region id="${regionIdPrefix + component}" scope="page" protected=true/>
         </#if>
      </#list>
      </div>
   </#list>
   </div>
</#macro>
