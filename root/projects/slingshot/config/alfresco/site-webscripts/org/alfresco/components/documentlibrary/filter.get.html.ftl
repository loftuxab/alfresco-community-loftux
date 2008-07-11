<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListFilter("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter doclib-filter">
   <h2>${msg("header.documents")}</h2>
   <ul class="filterLink">
      <li><span class="editingMe"><a href="#" onclick="return false;" class="filter-link">${msg("link.editingMe")}</a></span></li>
      <li><span class="editingOthers"><a href="#" onclick="return false;" class="filter-link">${msg("link.editingOthers")}</a></span></li>
      <li><span class="recentlyModified"><a href="#" onclick="return false;" class="filter-link">${msg("link.recentlyModified")}</a></span></li>
      <li><span class="recentlyAdded"><a href="#" onclick="return false;" class="filter-link">${msg("link.recentlyAdded")}</a></span></li>
   </ul>
</div>