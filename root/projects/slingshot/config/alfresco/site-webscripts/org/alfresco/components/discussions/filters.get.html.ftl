<script type="text/javascript">//<![CDATA[
   new Alfresco.TopicListFilter("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter topiclist-filter">
   <h2>${msg("header.browsetopics")}</h2>
   <ul class="filterLink">
      <li><span class="new"><a href="#" onclick="return false;" class="filter-link">${msg("link.new")}</a></span></li>
      <li><span class="hot"><a href="#" onclick="return false;" class="filter-link">${msg("link.hot")}</a></span></li>
      <li><span class="all"><a href="#" onclick="return false;" class="filter-link">${msg("link.all")}</a></span></li>
      <li><span class="mine"><a href="#" onclick="return false;" class="filter-link">${msg("link.mine")}</a></span></li>
   </ul>
</div>
