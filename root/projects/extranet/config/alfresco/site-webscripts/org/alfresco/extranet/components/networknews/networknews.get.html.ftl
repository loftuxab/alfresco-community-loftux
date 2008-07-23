<#if presentation = "list">

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

	p.readMore
	{
		float: right;
	}
	-->
	</style>
	<div class="networknews">
	   <div class="networknews-body scrollableList">
		<#if items?exists && items?size &gt; 0>

			<table>
			<#list items as i>			
			 <#if i_index < maxcount>
				
				<tr>
					<td valign="middle">
						<p class="networknews">
							<img src="${url.context}/proxy/alfresco-noauth/api/node/content/workspace/SpacesStore/${i.teaserImage.id}" alt="" class="networknews-image"/>
							<a href="<@link object='${i.nodeRef}'/>" class="networknews-title" target="_blank">${i.headline}</a>
						</p>
					</td>
				</tr>
				<tr><td><br/></td></tr>
				<tr>
					<td>
						<p class="networknews">
							${i.teaser}
						</p>

						<p class="readMore">
						<a href="<@link object='${i.nodeRef}'/>" class="networknews-title" target="_blank">
						${i.readMore}
						</a>
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

</#if>
<#if presentation="bullets">

	<ul>

	<#list items as i>			
	 <#if i_index < maxcount>

		<li>
			<a href="<@link object='${i.nodeRef}'/>" target="_blank">
				${i.headline}
			</a>
			(${i.modified})
		</li>

	 </#if>
	</#list>
	
	</ul>

</#if>
<#if presentation="grid22">

	<div class="yui-g">

		<div class="yui-u first">
		<#list items as item>
		
			<#if item_index < maxcount>

				<#if ((item_index + 1) % 2) == 0>
					</div>
					<div class="yui-u">
				</#if>

				<div class="box">
					<img src="${url.context}/proxy/alfresco-noauth/api/node/content/workspace/SpacesStore/${item.teaserImage.id}" alt="" />
					<h3><a href="<@link object='${item.nodeRef}'/>" class="networknews-title" target="_blank">${item.headline}</a></h3>
					${item.teaser}
					<a href="<@link object='${item.nodeRef}'/>" class="networknews-title" target="_blank">
					${item.readMore}
					</a>
				</div>

				<#if item_index == maxcount>
					<#break>
				</#if> 
			
			</#if>

		</#list>
		</div>

	</div>
	
</#if>