<style type="text/css">
<!--
.linkthru {
	background-color: white;
	color: black
	height: 14px;
	font-family: Verdana;
	font-size: 10px;
	padding: 0 0 4px 0;
	margin: 0 0 0 0;
}
.linkthru a {
	font-weight: bold;	
	color: #5ba1d0;
}
.header2 {
	background-color: #5ba1d0;
	color: #FFFFFF;
	height: 14px;
	padding: 2px 2px 2px 2px;
	font-family: Verdana;
	font-size: 10px;		
}
.header2 a {
	font-weight: bold;	
	color: #FFF;
}



.news-item-thumbnail {
	vertical-align: top;
}
.news-item-headline {
	padding: 1px 0 1px 0;
	margin: 0 0 0 0;
	vertical-align: top;
	font-family: Verdana;
	font-size: 10px;
}
.news-item-headline a {
	color:#5ba1d0;
	font-weight: none;
	font-size: 11px;
	text-decoration: none;
}




.plugin-thumbnail {
	vertical-align: top;
}

.plugin-headline {
	padding: 0 0 0 0;
	margin: 0 0 0 0;
	vertical-align: top;
	font-family: Verdana;
	font-size: 10px;	
}

.plugin-headline a {
	color:#5ba1d0;
	font-weight: bold;
	font-size: 11px;
	text-decoration: none;
	padding: 0 0 0 0;
	margin: 0 0 0 0;
}

.plugin-headline p {
	font-size: 10px;	
	padding: 4px 0 4px 0;
	margin: 0 0 0 0;
}

body
{
	padding: 0px 0px 0px 0px;
	margin: 0px 0px 0px 0px;
	border: 0px 0px 0px 0px;
}

-->
</style>


<!-- news section header -->
<table width="100%">
<tr>
<td class="header2">
	Latest News
</td>
</tr>
</table>

<#assign newsCount = 0>
<#list newsItems as newsItem>

	<#if newsCount < maxNewsCount>
	
		<table width="100%">
		<tr>
		<td class="news-item-thumbnail">
			<img src="${url.context}/images/extranet/info_16.gif" width="16" height="16" />
		</td>
		<td class="news-item-headline" width="100%">
			<a target="_blank" href="http://network.alfresco.com/extranet/?f=default&o=${newsItem.nodeRef}">
			${newsItem.headline}
			</a>
		</td>
		</tr>
		</table>
	
	</#if>
	
	<#assign newsCount = newsCount + 1>

</#list>

<!-- plugins section header -->
<table width="100%" style="margin-top: 4px;">
<tr>
<td class="header2">
	Latest Plugins / Uploads
</td>
</tr>
</table>

<#list assets as asset>

	<table width="100%">
	<tr>
	<td class="plugin-thumbnail">
		<img src="${url.context}/images/library/down18px.png" width="18" height="18" />
	</td>
	<td class="plugin-headline" width="100%">
		<a target="_blank" href="http://network.alfresco.com/extranet/?f=default&o=${asset.nodeRef}">
			${asset.title}
		</a>
		<p>
			${asset.headline}
		</p>
	</td>
	</table>

</#list>

<!-- join alfresco network header -->
<table width="100%" style="margin-top: 4px;">
<tr>
<td class="header2">
	Join Alfresco Network
</td>
</tr>
</table>


<!-- linkthru -->
<table width="100%">
<tr>
<td class="linkthru">
	Interested in finding out about Alfresco add-ons and extensions?
	<br/>
	<br/>Join
	<a target="_blank" href="http://network.alfresco.com?ref=30b1labs">Alfresco Network</a>
	today!
</td>
</tr>
</table>

