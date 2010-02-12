<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RuleDetails("${el}").setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${page.url.args.nodeRef!""}"),
      siteId: "${page.url.templateArgs.site!""}"            
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="rule-details">

   <div id="${el}-display" class="display" style="display: none;">
      <div class="actions">
         <input type="button" id="${el}-edit-button" value="${msg("button.edit")}" tabindex="0"/>
         <input type="button" id="${el}-delete-button" value="${msg("button.delete")}" tabindex="0"/>
      </div>

      <h2 id="${el}-name">&nbsp;</h2>
      <div>
         <em>${msg("label.description")}: </em><span id="${el}-description">&nbsp;</span>
      </div>
      <div>
         <em>${msg("label.modified")}: </em><span id="${el}-modified">&nbsp;</span>
      </div>

      <hr/>

      <div id="${el}-active" class="behaviour">${msg("label.active")}</div>
      <div id="${el}-runInBackground" class="behaviour">${msg("label.runInBackground")}</div>
      <div id="${el}-ruleAppliesToSubFolders" class="behaviour">${msg("label.ruleAppliesToSubFolders")}</div>

      <hr/>

      <div class="configuration-section when">
         <div class="configuration-header">
            <div class="configuration-title">${msg("header.when")}</div>
         </div>
         <ul class="configuration-body">
         </ul>
      </div>

      <div class="configuration-separator">&nbsp;</div>

      <div class="configuration-section if">
         <div class="configuration-header">
            <div class="configuration-title">${msg("header.if")}</div>
            <div class="configuration-relation and">
               <span class="and-label">${msg("label.and")}</span>
               <span class="or-label">${msg("label.or")}</span>
            </div>
         </div>
         <ul class="configuration-body">
         </ul>
      </div>

      <div class="configuration-section unless">
         <div class="configuration-header">
            <div class="configuration-title">${msg("header.unless")}</div>
            <div class="configuration-relation and">
               <span class="and-label">${msg("label.and")}</span>
               <span class="or-label">${msg("label.or")}</span>
            </div>
         </div>
         <ul class="configuration-body">
         </ul>
      </div>

      <div class="configuration-separator">&nbsp;</div>

      <div class="configuration-section action">
         <div class="configuration-header">
            <div class="configuration-title">${msg("header.action")}</div>
         </div>
         <ul class="configuration-body">
         </ul>
      </div>
   </div>
</div>
