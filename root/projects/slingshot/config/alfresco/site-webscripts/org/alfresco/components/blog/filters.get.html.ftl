<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostListFilters("${args.htmlid}");
//]]></script>

<div id="${args.htmlid}-body" class="filter menuTitle">
   <h2>${msg("postlist.filters.title")}</h2>
   <ul class="filterLink">
      <li id="${args.htmlid}-selectFilter-all" class="nav-label"><a href="" class="filter-link nav-link">${msg("postlist.filters.all")}</a></li>
      <li id="${args.htmlid}-selectFilter-new" class="nav-label"><a href="" class="filter-link nav-link">${msg("postlist.filters.new")}</a></li>
      <li id="${args.htmlid}-selectFilter-mydrafts" class="nav-label"><a href="" class="filter-link nav-link">${msg("postlist.filters.mydrafts")}</a></li>
      <li id="${args.htmlid}-selectFilter-mypublished" class="nav-label"><a href="" class="filter-link nav-link">${msg("postlist.filters.mypublished")}</a></li>
      <li id="${args.htmlid}-selectFilter-publishedext" class="nav-label"><a href="" class="filter-link nav-link">${msg("postlist.filters.publishedext")}</a></li>
   </ul>
</div>