<#assign el=args.htmlid?html>
<div id="${el}-body" class="cloud-manage-users">

   <!-- Header -->
   <div class="yui-g cloud-manage-users-header">
      <div class="yui-u first cloud-manage-users-header-title">
         <h1>${msg("header")}</h1>
      </div>
      <div class="yui-u cloud-manage-users-header-actions">
         <button id="${el}-newUser" type="alfresco-button">${msg("button.newUser")}</button>
      </div>
   </div>

   <!-- Toolbar -->
   <div class="yui-g cloud-manage-users-toolbar theme-bg-2">
      <div id="${el}-filters" class="yui-u first cloud-manage-users-toolbar-filters">
         <ul class="filterLink">
         <#list filters as filter>
            <li><span class="${filter.filterId}"><a class="filter-link" rel="${filter.filterData?js_string}" href="#">${filter.label?html}</a></span></li>
            <#if filter_has_next><span class="separator">&nbsp;</span></#if>
         </#list>
         </ul>
      </div>
      <div class="yui-u">
         &nbsp;
      </div>
   </div>

   <!-- Toolbar 2 -->
   <div class="yui-gb cloud-manage-users-toolbar2">
      <div class="yui-u first">
         &nbsp;
      </div>
      <div class="yui-u cloud-manage-users-toolbar2-paginator">
         <div id="${el}-paginator1" class="paginator">&nbsp;</div>
      </div>
      <div class="yui-u">
         &nbsp;
      </div>
   </div>

   <!-- User List -->
   <div id="${el}-list" class="cloud-manage-users-list"></div>

   <!-- Toolbar 2 bottom -->
   <div class="yui-gb cloud-manage-users-toolbar2">
      <div class="yui-u first">
         &nbsp;
      </div>
      <div class="yui-u cloud-manage-users-toolbar2-paginator">
         <div id="${el}-paginator2" class="paginator">&nbsp;</div>
      </div>
      <div class="yui-u">
         &nbsp;
      </div>
   </div>

</div>
<script type="text/javascript">//<![CDATA[
   new Alfresco.cloud.component.ManageUsers("${args.htmlid?js_string}").setOptions(
   {
      maxItems: ${maxItems!"50"},
      userFilters:
      {
         <#list filters as filter>"${filter.filterData}": "${filter.urlParameters}"<#if filter_has_next>,</#if></#list>
      }
   }).setMessages(
      ${messages}
   );
//]]></script>
