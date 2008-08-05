<script type="text/javascript">//<![CDATA[
   new Alfresco.TopicListFilters("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter topiclist-filter">
   <h2>${msg("topiclist.filters.title")}</h2>
   
   <ul class="filterLink">
      <li id="${args.htmlid}-selectFilter-new" class="nav-label"><a href="#" class="filter-link nav-link">${msg("topiclist.filters.new")}</a></li>
      <li id="${args.htmlid}-selectFilter-hot" class="nav-label"><a href="#" class="filter-link nav-link">${msg("topiclist.filters.hot")}</a></li>
      <li id="${args.htmlid}-selectFilter-all" class="nav-label"><a href="#" class="filter-link nav-link">${msg("topiclist.filters.all")}</a></li>
      <li id="${args.htmlid}-selectFilter-mine" class="nav-label"><a href="#" class="filter-link nav-link">${msg("topiclist.filters.my")}</a></li>
   </ul>
</div>
