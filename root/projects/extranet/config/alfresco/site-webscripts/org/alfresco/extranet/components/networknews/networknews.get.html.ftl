<style type="text/css">
<!--
div.networknews
{
}
div.networknews-body
{
	font: 12px arial;
	color: #000000;
}
div.networknews-element
{
	font: 12px arial;
	border: 1px black solid;
}
img.networknews-image
{
	float: left;
	height: 32px;
	width: 32px;
	margin: 6px 6px 6px 6px;
	border: 1px black solid;
}
a.networknews-title
{
	font: small-caps bold 14px arial;
	color: #000000;
}
a.networknews-title:link { color: #000000; text-decoration: none; }
a.networknews-title:visited { color: #000000; text-decoration: none; }
a.networknews-title:hover { color: #000000; text-decoration: none; }
a.networknews-title:active { color: #000000; text-decoration: none; }

p.networknews
{
	color: #000000;
}
-->
</style>
<div class="networknews">
   <div class="networknews-body scrollableList">
	<#if items?exists && items?size &gt; 0>
	
		<table>
		<#list items as i>
			<#if i.title?ends_with(".jpg")>
			<#else>
				<tr>
					<td valign="middle">
				<p class="networknews">
				<img src="${i.imageurl}" alt="" class="networknews-image"/>		
				<a href="${i.link}" class="networknews-title" target="_blank">${i.title}</a>
				</p>
					</td>
				</tr>
				<tr>
					<td>
			
				<p class="networknews">
					${i.description}
				</p>
					</td>
				</tr>
				<tr><td><br/></td></tr>
			</#if>
		</#list>
		</table>
	<#else>
		<em>No news items.</em>
	</#if>
   </div><#-- end of body -->
</div><#-- end of dashlet -->
