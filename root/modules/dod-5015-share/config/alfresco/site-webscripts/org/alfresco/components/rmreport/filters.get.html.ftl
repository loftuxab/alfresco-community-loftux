<script type="text/javascript">//<![CDATA[
   new Alfresco.ReportFilter("${args.htmlid}").setMessages(${messages});
//]]></script>
<div id="${args.htmlid}-body" class="filter">
   <h2>${msg("header.reports")}</h2>
   <ul class="filterLink">
      <li><span class="audit"><a href="#" onclick="return false;" class="filter-link">${msg("link.audit")}</a></span></li>
      <li><span class="notification"><a href="#" onclick="return false;" class="filter-link">${msg("link.notification")}</a></span></li>
      <li><span class="physical"><a href="#" onclick="return false;" class="filter-link">${msg("link.physical")}</a></span></li>
      <li><span class="review"><a href="#" onclick="return false;" class="filter-link">${msg("link.dueForReview")}</a></span></li>
      <li><span class="cutoff"><a href="#" onclick="return false;" class="filter-link">${msg("link.dueForCutOff")}</a></span></li>
      <li><span class="destroy"><a href="#" onclick="return false;" class="filter-link">${msg("link.dueForDestroy")}</a></span></li>
      <li><span class="transfer"><a href="#" onclick="return false;" class="filter-link">${msg("link.dueForTransfer")}</a></span></li>
      <li><span class="transferNARA"><a href="#" onclick="return false;" class="filter-link">${msg("link.dueForAscension")}</a></span></li>
   </ul>
</div>