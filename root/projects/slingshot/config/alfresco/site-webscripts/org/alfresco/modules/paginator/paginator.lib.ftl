<#--
   Contains all html snippets for the paginator component
-->

<#--
	Paginator renderer for the forum
--> 
<#macro renderForumPaginator pdata>
	${pdata.total} topics
	|
	Showing page ${pdata.currPage} of ${pdata.numPages}
	|
	<#if pdata.showFirst>
		<a href="#" id="GoToPrevious" class="pagerItem">&lt; Prev</a> 
	</#if>
	<#list pdata.pages as page>
		<a href="#" id="GoToPage-${page}" class="pagerItem">${page}</a>
	</#list>
	<#if pdata.showLast>
		<a href="#" id="GoToNext" class="pagerItem">Next &gt;</a> 
	</#if>
	<#-- Jump to NUM -->
</#macro>

<#-- Showing 10 of 100 topics | < Prev 1, 2, 3, 4, 5 ... 30 Next >  Jump to NUM -->

<#macro tmp paginatorData="">	
	<div id="bottomDiscussionsPagination">
		<div class="leftDiscussionBlogHeader">Showing <b>${topics?size}</b> of <b>${total}</b> topic<#if (total>1)>s</#if></div>
		<div class="centerDiscussionBlogHeader">
			<#if (nbPage > 1)>
				<div id="paginator">
					<#if (pos != 0)>
						<a href="#" id="GoToPage0" class="pagerItem">&lt;&lt;First</a> 
						<a href="#" id="GoToPage${previousPage}" class="pagerItem">&lt;Previous</a>
					</#if>
					<#list paging as page>
						${page}
					</#list>
					<#if ((pos + 1) != nbPage)>
						<a href="#" id="GoToPage${nextPage}" class="pagerItem">Next&gt;</a>
						<a href="#" id="GoToPage${lastPage}" class="pagerItem">Last&gt;&gt;</a>
					</#if>
				</div>
			</#if>
		</div>
		<div class="rightDiscussionBlogHeader">
			Detail list | <a href="#">Simple list</a>
		</div>
	</div>
</#macro>