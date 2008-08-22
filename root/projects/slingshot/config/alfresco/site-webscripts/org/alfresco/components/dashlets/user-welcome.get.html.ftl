<script type="text/javascript">//<![CDATA[
   new Alfresco.WelcomeDashlet("${args.htmlid}").setComponentId(
      "${instance.object.id}"
   );
//]]></script>
<div class="dashlet">
<div class="body">
   <div>
      <div style="float:right;">
         <button id="${args.htmlid}-remove-button">${msg("button.label")}</button>
      </div>
      <div style="float:left;">
         <h3>${msg("label.header")}!</h3>
         <p>${msg("label.intro")}...</p>
      </div>
   </div>
   <div style="clear:both">
     &nbsp;
   </div>
</div>
</div>

