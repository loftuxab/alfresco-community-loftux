<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListTree("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.args["site"]!""}"
   });
//]]></script>
<div id="${args.htmlid}-body" class="treeview doclib-filter">
   <h2>LIBRARY</h2>
   <div id="${args.htmlid}-treeview"></div>
</div>