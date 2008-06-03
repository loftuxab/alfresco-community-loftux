<ul>
	<li class="first"><a href="#">alfresco.com</a></li>
	
	<#list pages as page>
		<#assign activeTag="">
		<#if page.id == currentPageId>
			<#assign activeTag=" class='active' ">
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
