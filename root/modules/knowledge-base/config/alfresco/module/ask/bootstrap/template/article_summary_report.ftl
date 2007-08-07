<#-- Display the total number of articles and the articles by status -->

<link type="text/css" href="/alfresco/css/main.css" rel="stylesheet">

<#assign query="+ASPECT:\"{ask.new.model}article\"">
<#assign query = query + " +@cm\\:content.mimetype:\"text/xml\"">
<#assign articles = companyhome.childrenByLuceneSearch[query]>

<#assign draftArticleQuery=query + " +@ask\\:status:\"Draft\"">
<#assign draftArticles = companyhome.childrenByLuceneSearch[draftArticleQuery]>

<#assign pendingArticleQuery=query + " +@ask\\:status:\"Pending Approval\"">
<#assign pendingArticles = companyhome.childrenByLuceneSearch[pendingArticleQuery]>

<#assign currentArticleQuery=query + " +@ask\\:status:\"Current\"">
<#assign currentArticles = companyhome.childrenByLuceneSearch[currentArticleQuery]>

<#assign archivedArticleQuery=query + " +@ask\\:status:\"Archived\"">
<#assign archivedArticles = companyhome.childrenByLuceneSearch[archivedArticleQuery]>



<table width="100%">
    <tr class="recordSetHeader"><th align="left" colspan="2" >Article Status Report</th></th>
	<tr><td colspan="2">Total Articles: ${articles?size}</td></tr>
	<tr><td></td><td></td></tr>
	<tr style="font-size:130%;font-weight:bold;color:#0000FF;"><td>Status</td><td>Number</td></tr>
	<tr><td>Draft: </td><td>${draftArticles?size}</td></td>
	<tr><td>Pending Approval: </td><td>${pendingArticles?size}</td></td>
	<tr><td>Current: </td><td>${currentArticles?size}</td></td>
	<tr><td span="2"></td></tr>
	<tr><td>Archived: </td><td>${archivedArticles?size}</td></td>


</table>


