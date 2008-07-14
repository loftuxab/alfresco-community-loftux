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
    ${msg("paginator.totaltopics", pdata.total)}
	|
	${msg("paginator.showingpageof", pdata.currPage, pdata.numPages)}
	|
	<#if pdata.showFirst>
	<span id="${htmlid}-onFirstPage">
		<a href="#" class="paginator-action">&lt;&lt; ${msg("paginator.first")}</a> 
	</span>
	<span id="${htmlid}-onPreviousPage">
		<a href="#" class="paginator-action">&lt; ${msg("paginator.previous")}</a> 
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
		<a href="#" class="paginator-action">${msg("paginator.next")} &gt;</a>
	</span>
	<span id="${htmlid}-onLastPage">
		<a href="#" class="paginator-action">${msg("paginator.last")} &gt;&gt;</a>
	</span>
	</#if>
	
	<#-- Jump to NUM -->
</#macro>
