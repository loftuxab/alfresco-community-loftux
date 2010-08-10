<#include "/org/alfresco/components/form/controls/association.ftl" />

<#macro setPackageItemOptions field>

   <#-- Remove all items action -->

   <#-- Remove item action -->
   <#local allowAddAction = false>
   <#local allowRemoveAllAction = false>
   <#local allowRemoveAction = false>
   <#local actions = []>

   <#-- Add item action -->
   <#if form.data['prop_bpm_packageActionGroup']?? && form.data['prop_bpm_packageActionGroup']?is_string && form.data['prop_bpm_packageActionGroup']?length &gt; 0>
      <#local allowAddAction = true>
   </#if>

   <#if form.data['prop_bpm_packageItemActionGroup']?? && form.data['prop_bpm_packageItemActionGroup']?is_string && form.data['prop_bpm_packageItemActionGroup']?length &gt; 0>
      <#local packageItemActionGroup = form.data['prop_bpm_packageItemActionGroup']>
      <#local viewMoreActionsLink>function(item){ return Alfresco.constants.URL_PAGECONTEXT + (item.site ? "site/" + item.site + "/": "") + "document-details?nodeRef=" + item.nodeRef; }</#local>
      <#local viewMoreAction = { "name": "view_more_document_actions", "label": "form.control.object-picker.workflow.view_more_document_actions", "link": viewMoreActionsLink }>
      <#if packageItemActionGroup == "read_package_item_actions" || packageItemActionGroup == "edit_package_item_actions">
         <#local actions = actions + [viewMoreAction]>
      <#elseif packageItemActionGroup == "remove_package_item_actions" || packageItemActionGroup == "start_package_item_actions" || packageItemActionGroup == "edit_and_remove_package_item_actions">
         <#local actions = actions + [viewMoreAction]>
         <#local allowRemoveAllAction = true>
         <#local allowRemoveAction = true>
      <#elseif packageItemActionGroup >
      <#else>
         <#local actions = actions + [viewMoreAction]>      
      </#if>
   </#if>



   <#-- Additional item actions -->

   <script type="text/javascript">//<![CDATA[
   (function()
   {
      <#-- Modify the properties on the object finder created by association control-->
      var picker = Alfresco.util.ComponentManager.get("${controlId}");
      picker.setOptions(
      {
         itemType: "cm:content",
         displayMode: "${field.control.params.displayMode!"list"}",
         listItemActions: [
         <#list actions as action>
            {
               name: "${action.name}",
               <#if action.link??>
               link: ${action.link},
               <#elseif action.event>
               event: "${action.event}", 
               </#if>
               label: "${action.label}"
            }<#if action_has_next>,</#if>
         </#list>
         ],
         allowRemoveAction: ${allowRemoveAction?string},
         allowRemoveAllAction: ${allowRemoveAllAction?string},
         allowAddAction: ${allowAddAction?string}
      });
   })();
   //]]></script>

</#macro>
<@setPackageItemOptions field/>