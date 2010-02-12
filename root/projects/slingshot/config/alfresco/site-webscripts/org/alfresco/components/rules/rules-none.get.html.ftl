<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RulesNone("${el}").setOptions(
   {
      nodeRef: "${page.url.args.nodeRef!""}",
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="rules-none">
   <div class="yui-g">
      <div class="yui-u first">
         <h2>${msg("header.create-rule")}</h2>
         <div>${msg("text.create-rule")}</div>
         <input type="button" id="${el}-createRule-button" value="${msg("button.create-rule")}" tabindex="0"/>
      </div>
      <div class="yui-u">
         <h2>${msg("header.link-to-rule-set")}</h2>
         <div>${msg("text.link-to-rule-set")}</div>
         <input type="button" id="${el}-linkToRuleSet-button" value="${msg("button.link-to-rule-set")}" tabindex="0"/>
      </div>
   </div>
</div>
