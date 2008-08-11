<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostListFilter("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter blog-filter">
   <h2>${msg("header.browseposts")}</h2>
   <ul class="filterLink">
      <li><span class="all"><a href="#" onclick="return false;" class="filter-link">${msg("link.all")}</a></span></li>
      <li><span class="new"><a href="#" onclick="return false;" class="filter-link">${msg("link.new")}</a></span></li>
      <li><span class="mydrafts"><a href="#" onclick="return false;" class="filter-link">${msg("link.mydrafts")}</a></span></li>
      <li><span class="mypublished"><a href="#" onclick="return false;" class="filter-link">${msg("link.mypublished")}</a></span></li>
      <li><span class="publishedext"><a href="#" onclick="return false;" class="filter-link">${msg("link.publishedext")}</a></span></li>
   </ul>
</div>