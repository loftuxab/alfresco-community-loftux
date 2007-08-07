<table width="100%">
<tr><td>

<table width="100%">
<tr><td colspan="2"  style="font-size:130%" class="recordSetHeader">Please select a link below</tr>
<tr>
<td>
<a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/create_article.ftl&contextPath=/Company%20Home/Data%20Dictionary/Knowledge%20Base" target="Create_Article">Create Article</a>
</td>
<td>
<a href="/alfresco/template?templatePath=/Company%20Home/Data%20Dictionary/Knowledge%20Base/template/search_articles.ftl&contextPath=/Company%20Home/Data%20Dictionary/Knowledge%20Base" target="Search">Current Article Search</a>
</td>
</tr>
</table>

</td>
</tr>
<tr>

<td>
<#-- Include article status summary report -->
<#assign articleSummaryReport = companyhome.childByNamePath["Data Dictionary/Knowledge Base/template/article_summary_report.ftl"] >
<#include articleSummaryReport.id>
</td>
</tr>

<tr>
<td>
<#assign articleUsageReport= companyhome.childByNamePath["Data Dictionary/Knowledge Base/template/article_usage_report.ftl"] >
<#include articleUsageReport.id >
</td>
</tr>
</table>