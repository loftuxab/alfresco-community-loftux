<script type="text/javascript">//<![CDATA[
   new Alfresco.DiscussionsTopicListFilters("${args.htmlid}").setOptions(
   {
   }).setMessages(
      ${messages}
   );
//]]></script>
<div id="${args.htmlid}-body" class="filter menuTitle">
   <h2>BROWSE TOPICS</h2>
   <ul class="filterLink">
      <li id="${args.htmlid}-showNewTopics" class="nav-label"><a href="" class="filter-link nav-link">${msg("menu.filter.new")}</a></li>
      <li id="${args.htmlid}-showHotTopics" class="nav-label"><a href="" class="filter-link nav-link">${msg("menu.filter.hot")}</a></li>
      <li id="${args.htmlid}-showAllTopics" class="nav-label"><a href="" class="filter-link nav-link">${msg("menu.filter.all")}</a></li>
      <li id="${args.htmlid}-showMyTopics" class="nav-label"><a href="" class="filter-link nav-link">${msg("menu.filter.my")}</a></li>
   </ul>
</div>