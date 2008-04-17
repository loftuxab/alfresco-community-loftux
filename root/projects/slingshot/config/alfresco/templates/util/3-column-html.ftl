<#--
  To accomplish the 3 column layout the divs must be placed in the order of left, right, middle.
  Therefore the following loop approach is used:
  On loop column #1: A "left" region is placed
  On loop column #2: A "right" region is placed (if there is one)
  On loop column #2: A "middle" region is placed
  On loop column #3/#0: In this loop no regions are placed since the right one was placed on loop #2, however a div with
                     class "clear" is placed to make sure the next rows columns will start on the correct height
-->
<#assign columns = 3/>
<#list 1..regions as regionIndex>
   <#assign column = regionIndex % columns/>

   <#if column == 1>
      <div class="left">
         <!-- COMP START -->
         <@region id="component-${regionIndex}" scope="page"/>
         <!-- COMP END -->
      </div>
   <#elseif column == 2>
      <#if regionIndex_has_next && ((column + 1) % columns == 0)>
      <div class="right">
         <!-- COMP START -->
         <@region id="component-${regionIndex + 1}" scope="page"/>
         <!-- COMP END -->
      </div>
      </#if>
      <#if column == 2>
      <div class="middle">
         <!-- COMP START  -->
         <@region id="component-${regionIndex}" scope="page"/>
         <!-- COMP END -->
      </div>
      </#if>
   <#elseif column == 0>
      <div class="column-clear">&nbsp;</div>
   </#if>

</#list>

