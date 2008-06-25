<script type="text/javascript">//<![CDATA[
	new Alfresco.Wiki("${args.htmlid}").setSiteId(
		"${page.url.args["site"]}"
	).setPageTitle(
		"${page.url.args["title"]}"
	);
//]]></script>
<div id="${args.htmlid}-wikipage" class="yui-navset"> 
	    <ul class="yui-nav"> 
	        <li class="selected"><a href="#page"><em>Page</em></a></li> 
	        <li><a href="#edit"><em>Edit</em></a></li>
	    </ul>             
	    <div class="yui-content"> 
	        <div id="#page"><#if result?exists>${result}</#if></div> 
	        <div id="#edit">
			<textarea name="${args.htmlid}-pagecontent" id="${args.htmlid}-pagecontent" cols="50" rows="10"> 
			<#if result?exists>${result}</#if>
			</textarea>
			<div>
	            <input type="submit" id="${args.htmlid}-save-button" value="Save" />
				<input type="submit" id="${args.htmlid}-cancel-button" value="Cancel" />
	        </div>
			</div> 
	    </div> 
</div>
