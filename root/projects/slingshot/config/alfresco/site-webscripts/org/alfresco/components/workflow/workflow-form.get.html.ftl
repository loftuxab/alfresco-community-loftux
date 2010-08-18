<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
new Alfresco.WorkflowForm("${el}").setOptions({}).setMessages(${messages});
//]]></script>
<div id="${el}-body" class="workflow-form">
</div>
<div class="hidden">
<#--
 The workflow details page form is actually a form display of the workflow's start task and data form the workflow itself.
 The approach taken to display all this information is described in the Alfresco.WorkflowForm javascript class.
-->

   <#-- Will be inserted in the top of the form after its been loaded through ajax -->
   <div id="${el}-summary-form-section">
      <div class="set-title">${msg("header.summary")}</div>
      <div class="form-element-background-color form-element-border">
         <div class="yui-g">
            <div class="yui-u first">
               <div id="${el}-statusSummary"></div>
               <div id="${el}-dueSummary"></div>
               <div id="${el}-prioritySummary"></div>
            </div>
         </div>
         <div class="yui-u">
            <h3>${msg("label.mostRecentlyCompletedTask")}</h3>            
         </div>
      </div>
   </div>

   <#-- Will be inserted above "More Info" in the form after its been loaded through ajax -->
   <div id="${el}-general-form-section">
      <div class="set-title">${msg("header.general")}</div>
      <div class="form-field">
         <div class="viewmode-field">
            <span class="viewmode-label">${msg("label.title")}:</span>
            <span class="viewmode-value" id="${el}-title"></span>
         </div>
      </div>
      <div class="form-field">
         <div class="viewmode-field">
            <span class="viewmode-label">${msg("label.description")}:</span>
            <span class="viewmode-value" id="${el}-description"></span>
         </div>
      </div>
      <div class="yui-gb">
         <div class="yui-u first">
            <div class="form-field">
               <div class="viewmode-field">
                  <span class="viewmode-label">${msg("label.startedBy")}:</span>
                  <span class="viewmode-value" id="${el}-startedBy"></span>
               </div>
            </div>
         </div>
         <div class="yui-u">
            <div class="form-field">
               <div class="viewmode-field">
                  <span class="viewmode-label">${msg("label.due")}:</span>
                  <span class="viewmode-value" id="${el}-due"></span>
               </div>
            </div>
         </div>
         <div class="yui-u">
            <div class="form-field">
               <div class="viewmode-field">
                  <span class="viewmode-label">${msg("label.completed")}:</span>
                  <span class="viewmode-value" id="${el}-completed"></span>
               </div>
            </div>
         </div>
      </div>
      <div class="yui-gb">
         <div class="yui-u first">
            <div class="form-field">
               <div class="viewmode-field">
                  <span class="viewmode-label">${msg("label.started")}:</span>
                  <span class="viewmode-value" id="${el}-started"></span>
               </div>
            </div>
         </div>
         <div class="yui-u">
            <div class="form-field">
               <div class="viewmode-field">
                  <span class="viewmode-label">${msg("label.priority")}:</span>
                  <span class="viewmode-value" id="${el}-priority"></span>
               </div>
            </div>
         </div>
         <div class="yui-u">
            <div class="form-field">
               <div class="viewmode-field">
                  <span class="viewmode-label">${msg("label.type")}:</span>
                  <span class="viewmode-value" id="${el}-type"></span>
               </div>
            </div>
         </div>
      </div>
      <div class="form-field">
         <div class="viewmode-field">
            <span class="viewmode-label">${msg("label.status")}:</span>
            <span class="viewmode-value" id="${el}-status"></span>
         </div>
      </div>
   </div>

   <#-- Will be inserted below "Items" in the form after its been loaded through ajax -->
   <div id="${el}-currentTasks-form-section" class="current-tasks">
      <h3>${msg("header.currentTasks")}</h3>
      <div class="form-element-background-color"></div>
   </div>

   <#-- Will be inserted in the bottom of the form after its been loaded through ajax -->
   <div id="${el}-workflowHistory-form-section" class="workflow-history">
      <h3>${msg("header.workflowHistory")}</h3>
      <div class="form-element-background-color"></div>
   </div>

</div>