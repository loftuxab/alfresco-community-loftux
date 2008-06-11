<script type="text/javascript">//<![CDATA[
   new Alfresco.DocListFilter("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter doclib-filter">
   <h2>${msg("header.documents")}</h2>
   <ul class="filterLink">
      <li class="recentlyModified"><a href="#" onclick="return false;" class="filter-link">${msg("link.recentlyModified")}</a></li>
      <li class="recentlyAdded"><a href="#" onclick="return false;" class="filter-link">${msg("link.recentlyAdded")}</a></li>
      <li class="iAmEditing"><a href="#" onclick="return false;" class="filter-link">${msg("link.editedByMe")}</a></li>
      <li class="othersAreEditing"><a href="#" onclick="return false;" class="filter-link">${msg("link.editedByOthers")}</a></li>
   </ul>
</div>