<#--
   Contains all html snippets for the paginator component
-->

<#macro renderPaginatorModule htmlid pdata>
<script type="text/javascript">//<![CDATA[
   new Alfresco.Paginator("${htmlid}").setOptions(
   {
      startIndex : ${pdata.startIndex},
      itemCount : ${pdata.itemCount},
      total : ${pdata.total},
      pageSize : ${pdata.pageSize}
   }).setMessages(
      ${messages}
   );
//]]></script>
<span id="${htmlid}-paginator">
<@renderPaginatorContent htmlid pdata />
</span>
</#macro>


<#macro getPaginatorUpdateJSONData htmlid pdata>
{
   "startIndex" : ${pdata.startIndex},
   "pageSize" : ${pdata.pageSize},
   "itemCount" : ${pdata.itemCount},
   "total" : ${pdata.total},
   <#assign paginatorHtml><@paginatorLib.renderPaginatorContent htmlid pdata /></#assign>
   "html" : "${paginatorHtml?j_string}"
}
</#macro>


<#--
	Paginator renderer for the forum
--> 
<#macro renderPaginatorContent htmlid pdata>
	${pdata.total} topics
	|
	Showing page ${pdata.currPage} of ${pdata.numPages}
	|
	<#if pdata.showFirst>
	<span id="${htmlid}-onFirstPage">
		<a href="#" class="paginator-action">&lt;&lt; First</a> 
	</span>
	<span id="${htmlid}-onPreviousPage">
		<a href="#" class="paginator-action">&lt; Prev</a> 
	</span>
	</#if>
	<span id="${htmlid}-pages">
	<#list pdata.pages as page>
		<span id="${htmlid}-onGoToPage-${page}">
			<a href="#" class="paginator-action">${page}</a>
		</span>
	</#list>
	</span>
	<#if pdata.showLast>
	<span id="${htmlid}-onNextPage">
		<a href="#" class="paginator-action">Next &gt;</a>
	</span>
	<span id="${htmlid}-onLastPage">
		<a href="#" class="paginator-action">Last &gt;&gt;</a>
	</span>
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