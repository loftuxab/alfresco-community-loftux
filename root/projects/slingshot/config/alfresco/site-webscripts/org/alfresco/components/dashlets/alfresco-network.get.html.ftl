<script type="text/javascript">//<![CDATA[
   new Alfresco.widget.DashletResizer("${args.htmlid}", "${instance.object.id}");
//]]></script>
<div class="dashlet">
   <div class="title">${msg("header.network")}</div>
   <div class="body" id="alfresco-network">
      <iframe width="100%" height="256" src="${url}" style="border:0; overflow-x: hidden; overflow-y: scroll;"></iframe>
   </div>
</div>