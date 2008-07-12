<style type="text/css">
<!--
#nav-selected {
	font: 1.153em/2em 'Trebuchet MS', Arial, sans-serif;
	background: url('../images/spr-nav.gif') no-repeat 0 50%;
}
-->
</style>
<ul>
	<#list pages as page>
		<#assign activeTag="">
		<#if page.id == currentPageId>
			<#assign activeTag=" id='nav-selected' ">
		</#if>
		
		<li ${activeTag}>
			<@anchor page="${page.id}">
				${page.title}
			</@anchor>
		</li>
	</#list>
</ul>
<span class="lt"></span>
<span class="rt"></span>
<span class="lb"></span>
<span class="rb"></span>
