<script type="text/javascript">//<![CDATA[
   new Alfresco.DataList('${args.htmlid}').setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}",
      containerId: "${template.properties.container!"dataLists"}",
      datalistId:'1',
      columnDefs: [
         {key:"title", label:'Title', sortable:true, editor: new YAHOO.widget.TextboxCellEditor(), resizeable:true},
         {key:"duedate", label: 'Due Date', sortable:true, formatter:"date",  editor: new YAHOO.widget.DateCellEditor(), resizeable:true},
         {key:"priority", label:'Priority', sortable:true, editor: new YAHOO.widget.TextboxCellEditor(), resizeable:true}
      ],
      schema: {
         resultsList: "items",
         fields: [
            {key:"title", parser:YAHOO.util.DataSource.parseString}, 
            {key:"duedate", parser:"date"},
            {key:"priority" }
         ]
      }
   }).setMessages(${messages});
//]]></script>
<div id="${args.htmlid}-body" class="datalists">
   <h2>${msg('Fruit list')}</h2>
   <div id="${args.htmlid}-grid" class="yui-dt">
      
   </div>
   <!-- row item action html -->
   <div id="${args.htmlid}-grid-row-actions" class="datalist-grid-row-actions"></div>
   <!-- dialog toBeMoved -->
   <div id="template-datalist-dialog" class="crud-datalist">
      <div id="template-datalist-dialogTitle" class="hd">Edit Row</div>
      <div class="bd">

         <div id="template-datalist-form-container" class="form-container">

            <!-- <div class="yui-g">
               <h2 id="template-datalist-dialogHeader">header</h2>
            </div> -->
   
      
            <form id="template-datalist-form" method="POST" accept-charset="utf-8" enctype="application/json" action="#">
   
               <input id="template-datalist-form-destination" name="alf_destination" type="hidden" value="workspace://SpacesStore/12762d71-2e58-4af7-a8a5-8c1d0d8ecf3d" />
   
               <div id="template-datalist-form-fields" class="form-fields">
   
               <div class="form-field">
                        <label for="template-datalist_prop_cm_title">Title:<span class="mandatory-indicator">*</span></label>
                     <input id="template-datalist_prop_cm_title" type="text" name="prop_cm_title" value="" title="Name" />
               </div>
               <div class="form-field">
                     <label for="template-datalist_prop_cm_duedate">Due Date:</label>
                     <input id="template-datalist_prop_cm_duedate" name="prop_cm_duedate" type="text" title="Content Description" ></input>
               </div>
               <div class="form-field">
                  <label for="template-datalist_prop_cm_priority">Priority:</label>
                  <input id="template-datalist_prop_cm_priority" name="prop_cm_priority" type="text" title="Content Title"></input>
               </div>   
               </div>
               <div class="bdft">
                  <input id="template-datalist-form-submit" type="submit" value="Save" />
                  &nbsp;<input id="template-datalist-form-cancel" type="button" value="Cancel" />
               </div>
      
            </form>

         </div>
      </div>   
   </div>
</div>