<style type="text/css">
<!--
div.rssfeed
{
	background-color: white;
}
div.rssfeed-title
{
	padding: 10px 0 0 10px;
	font: bold 30px arial;
}
div.rssfeed-body
{
	padding: 10px;
}
div.rssfeed-body p
{
	padding: 0 0 20px 0;
}
div.rssfeed-body h4
{
	font: bold 16px arial;
}
-->
</style>
<div class="rssfeed">
   <div class="rssfeed-title">${title!""}</div>
   <div class="rssfeed-body scrollableList">
	<#if items?exists && items?size &gt; 0>
		<#list items as i>
		<p>
		<h4><a href="${i.link}" target="_blank">${i.title}</a></h4>
		${i.description}
		</p>
		</#list>
	<#else>
		<em>No news items.</em>
	</#if>
	</div><#-- end of body -->
</div>
