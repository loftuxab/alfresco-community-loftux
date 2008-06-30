<script type="text/javascript">//<![CDATA[
	new Alfresco.Wiki("${args.htmlid}").setSiteId(
		"${page.url.args["site"]}"
	).setPageTitle(
		"${page.url.args["title"]}"
	);
//]]></script>
<div id="${args.htmlid}-wikipage" style="visibility: hidden;" class="yui-navset"> 
	    <ul class="yui-nav"> 
	        <li class="selected"><a href="#page"><em>Page</em></a></li> 
	        <li><a href="#edit"><em>Edit</em></a></li>
			<li><a href="#history"><em>History</em></a></li>
	    </ul>             
	    <div class="yui-content" style="background: #FFFFFF;"> 
	        <div id="#page"><#if result?exists>${result.pagetext}</#if></div> 
	        <div id="#edit">
			<textarea name="${args.htmlid}-pagecontent" id="${args.htmlid}-pagecontent" cols="50" rows="10"> 
			<#if result?exists>${result.pagetext}</#if>
			</textarea>
			<div>
	            <input type="submit" id="${args.htmlid}-save-button" value="Save" />
				<input type="submit" id="${args.htmlid}-cancel-button" value="Cancel" />
	        </div>
			</div> 
			<div id="#history">
			<#if result.versionhistory?exists>
			<table id="versionhistory" style="width: 50%">
			<tr>
			<th>Version</th><th>Date</th><th>Author</th>
			</tr>
			<#list result.versionhistory as version>
			<tr><td>${version.version}</td><td>${version.date}</td><td>${version.author}</td></tr>
			</#list>
			</table>
			</#if>
			</div>
	    </div> 
</div>
