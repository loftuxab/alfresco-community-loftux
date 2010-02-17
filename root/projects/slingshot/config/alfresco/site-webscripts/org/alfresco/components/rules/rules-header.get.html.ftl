<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RulesHeader("${el}").setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${page.url.args.nodeRef!""}"),
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="rules-header">
   <div class="yui-g">
      <div class="yui-u first rules-title">
         <h1><span id="${el}-title"></span>: ${msg("header.rules")}</h1>
      </div>
      <div id="${el}-actions" class="yui-u rules-actions">
         <span id="${el}-inheritRules-container" class="inherit">
            <button id="${el}-inheritRules-button" tabindex="0">${msg("button.inherit-rules")}</button>
         </span>
         <span class="separator">&nbsp;</span>
         <button class="new" id="${el}-newRule-button" tabindex="0">${msg("button.new-rule")}</button>
         <button class="copy" id="${el}-copyRuleFrom-button" tabindex="0">${msg("button.copy-rule-from")}</button>
         <button class="run" id="${el}-runRules-menu" tabindex="0">${msg("menu.run")}</button>
         <select class="run-menu" id="${el}-runRules-options">
            <option value="run">${msg("menu.option.run")}</option>
            <option value="run-recursive">${msg("menu.option.run-recursive")}</option>
         </select>

      </div>
   </div>
</div>
