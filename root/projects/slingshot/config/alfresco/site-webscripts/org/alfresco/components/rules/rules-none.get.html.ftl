<#assign el=args.htmlid>
<script type="text/javascript">//<![CDATA[
   new Alfresco.RulesNone("${el}").setOptions(
   {
      nodeRef: new Alfresco.util.NodeRef("${page.url.args.nodeRef!""}"),
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${el}-body" class="rules-none theme-bg-color-6 theme-border-3">
   <h2>${msg("header")}</h2>
   <div class="dialog-option">
      <a href="rule-edit?nodeRef=${page.url.args.nodeRef!""}">${msg("header.create-rule")}</a>
      <div>${msg("text.create-rule")}</div>
   </div>
   <div class="dialog-option">
      <a id="${el}-linkToRuleSet" href="#">${msg("header.link-to-rule-set")}</a>
      <div>${msg("text.link-to-rule-set")}</div>
   </div>
</div>
