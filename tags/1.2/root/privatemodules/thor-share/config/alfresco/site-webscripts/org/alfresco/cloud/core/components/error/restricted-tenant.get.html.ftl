<#assign el=args.htmlid?html/>
<div id="${el}-body" class="cloud-restricted-tenant theme-overlay hidden" style="padding: 1em;">
   <div class="theme-company-logo"></div>
   <div class="theme-border-3 theme-bg-color-8" style="padding: 1em;">
      <h2 class="theme-color-1 thin error">${msg("header")}</h2>
      <hr/>
      <p>${msg("text")}</p>
      <p>
         ${msg("home", url.context?matches("/[^/]+")[0])}
      </p>
   </div>
</div>

<script type="text/javascript">
   (function()
   {
      var overlay = Alfresco.util.createYUIOverlay(Dom.get("${args.htmlid?js_string}"),
      {
         fixedcenter: true,
         effect:
         {
            effect: YAHOO.widget.ContainerEffect.FADE,
            duration: 0.25
         }
      }, { render: false });
      Dom.removeClass("${args.htmlid?js_string}-body", "hidden");
      overlay.render(document.body);
      overlay.center();
      overlay.show();
   })();
</script>