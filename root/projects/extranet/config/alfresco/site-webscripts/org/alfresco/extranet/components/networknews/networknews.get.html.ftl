<#if displayOption?exists>

	<#if displayOption == "fourgrid">
	
		<#if items?exists>

		<#assign index = 0>
		
		<div class="yui-g">
			<div class="yui-u first">
				<div class="box">
				
					<#if items?size &gt; index>
						
						<img src="${items[index].imageurl}" alt="" />
						<h3>${items[index].title}</h3>
						${items[index].description}
						<br/>
						<a href="${items[index].link}">Read more!</a>
					
						<#assign index = index + 1>
					</#if>

				</div>
				<div class="box">

					<#if items?size &gt; index>
						
						<img src="${items[index].imageurl}" alt="" />
						<h3>${items[index].title}</h3>
						${items[index].description}
						<br/>
						<a href="${items[index].link}">Read more!</a>
					
						<#assign index = index + 1>
					</#if>

				</div>
			</div>
			<div class="yui-u">
				<div class="box">

					<#if items?size &gt; index>
						
						<img src="${items[index].imageurl}" alt="" />
						<h3>${items[index].title}</h3>
						${items[index].description}
						<br/>
						<a href="${items[index].link}">Read more!</a>
					
						<#assign index = index + 1>
					</#if>

				</div>
				<div class="box">
				
					<#if items?size &gt; index>
						
						<img src="${items[index].imageurl}" alt="" />
						<h3>${items[index].title}</h3>
						${items[index].description}
						<br/>
						<a href="${items[index].link}">Read more!</a>
					
						<#assign index = index + 1>
					</#if>

				</div>
			</div>
		</div>
		
		</#if>
	
	</#if>
<#else>

	<div class="rssfeed">
	   <div class="title">${title!""}</div>
	   <div class="body scrollableList">
		<#if items?exists && items?size &gt; 0>
			<#list items as i>
			<p>
			<h4><a href="${i.link}">${i.title}</a></h4>
			${i.description}
			</p>
			</#list>
		<#else>
			<em>No news items.</em>
		</#if>
		</div><#-- end of body -->
	</div><#-- end of dashlet -->

</#if>
