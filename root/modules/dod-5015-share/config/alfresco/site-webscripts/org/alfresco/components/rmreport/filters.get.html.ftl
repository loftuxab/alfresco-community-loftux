<script type="text/javascript">//<![CDATA[
   new Alfresco.ReportFilter("${args.htmlid}");
//]]></script>
<div id="${args.htmlid}-body" class="filter">
   <h2>${msg("header.reports")}</h2>
   <ul class="filterLink">
      <li><span class="audit"><a href="#" onclick="return false;" class="filter-link">${msg("link.audit")}</a></span></li>
      <li><span class="notification"><a href="#" onclick="return false;" class="filter-link">${msg("link.notification")}</a></span></li>
      <li><span class="physical"><a href="#" onclick="return false;" class="filter-link">${msg("link.physical")}</a></span></li>
      <li><span class="request"><a href="#" onclick="return false;" class="filter-link">${msg("link.request")}</a></span></li>
      <li><span class="retention"><a href="#" onclick="return false;" class="filter-link">${msg("link.retention")}</a></span></li>
      <li><span class="review"><a href="#" onclick="return false;" class="filter-link">${msg("link.review")}</a></span></li>
   </ul>
</div>