<script type="text/javascript">//<![CDATA[
   new Alfresco.BlogPostListArchives("${args.htmlid}");
//]]></script>

<div id="archives-body" class="filter menuTitle">
	<h2>BROWSE ARCHIVES</h2>
    <ul class="filterLink">
        <#list items as month>
            <li class="onMonthSelection nav-label" id="${args.htmlid}-onMonthSelection-${month.year?c}-${month.month?c}">
                <a  href="" class="archive-link nav-link">${month.beginOfMonth?string("MMMM yyyy")}</a> (${month.postCount?c})
            </li>
        </#list>
	</ul>
</div>