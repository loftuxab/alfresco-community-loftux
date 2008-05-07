<ul>
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

	<!--	
	<li class="active">Home</li>
	<li><@anchor page="page.issue-tracking">Issue Tracking</@anchor></li>
	<li><@anchor page="page.knowledge-base">Knowledge Base</@anchor></li>
	<li><@anchor page="page.wiki">Wiki</@anchor></li>
	<li><@anchor page="page.adobe-connect">Adobe Connect</@anchor></li>
	-->
	<!--
	<li class="active">Home</li>
	<li><a href="http://forums.alfresco.com/" accesskey="i">Forums</a></li>
	<li><a href="http://sandbox.alfrescodemo.com:8080/alfresco/service/addOnDetail/folder/Company%20Home/Web%20Scripts" accesskey="m">Extensions</a></li>
	<li><a href="http://wiki.alfresco.com/" accesskey="m">Wiki</a></li>
	<li><a href="http://hosted4.alfresco.com/alfresco/navigate/browse/workspace/SpacesStore/305fd4cc-ab0e-11db-8274-37234b013900" accesskey="p">Library</a></li>
	<li><a href="http://blogs.alfresco.com/" accesskey="s">Blogs</a></li>
	<li><a href="http://issues.alfresco.com/" accesskey="r">Issues</a></li>
	<li><a href="#" accesskey="s">Contribute</a></li>
	<li><a href="#" accesskey="r">Partners</a></li>
	-->
</ul>
