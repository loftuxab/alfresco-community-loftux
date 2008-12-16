<script type="text/javascript">//<![CDATA[
   new Alfresco.LinkFilter("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter links-filter">
   <h2>${msg("header.links")}</h2>
   <ul class="filterLink">
      <li class="first-link"><span class="all"><a href="#" onclick="return false;" class="filter-link">${msg("link.all")}</a></span></li>
      <li><span class="internal"><a href="#" onclick="return false;" class="filter-link">${msg("link.internal")}</a></span></li>
      <li><span class="www"><a href="#" onclick="return false;" class="filter-link">${msg("link.www")}</a></span></li>
   </ul>
</div>
