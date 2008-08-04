<script type="text/javascript">//<![CDATA[
	new Alfresco.Wiki("${args.htmlid}").setSiteId(
		"${page.url.templateArgs.site}"
	).setPageTitle(
		"${page.url.args["title"]!""}"
	);
//]]></script>
<div id="${args.htmlid}-wikipage" style="visibility: hidden;" class="yui-navset"> 
      <h1>${page.url.args["title"]?replace("_", " ")}</h1>
	    <ul class="yui-nav"> 
	    <#assign action = page.url.args["action"]!"view">   
	    <#assign tabs =
	    [
	      { 
	         "id": "#page",
	         "label": msg("tab.view"),
	         "action": "view"
	      },
	      {
	         "id": "#edit",
	         "label": msg("tab.edit"),
	         "action": "edit"
	      },
	      {
	         "id": "#history",
	         "label": msg("tab.details"),
	         "action": "details"
	      }
	    ]>     
	    <#list tabs as tab>
	      <li <#if tab.action == action>class="selected"</#if>><a href="${tab.id}"><em>${tab.label}</em></a></li> 
	    </#list>
	    </ul>             
	    <div class="yui-content" style="background: #FFFFFF;"> 
	        <div id="#page"><#if result?exists>${result.pagetext}</#if></div> 
	        <div id="#edit">
	            <form id="${args.htmlid}-form" action="${page.url.context}/proxy/alfresco/slingshot/wiki/page/${page.url.templateArgs.site}/${page.url.args["title"]}" method="POST">
	            <#assign pageContext = page.url.context + "/page/site" + page.url.templateArgs.site + "/wiki-page?title=" + page.url.args["title"]>
	            <input type="hidden" name="context" value="${pageContext?html}/" />
               <textarea name="pagecontent" id="${args.htmlid}-pagecontent" cols="50" rows="10"><#if result?exists>${result.pagetext}</#if></textarea>
               <!-- tags -->
               <#if result.tags?? && result.tags?size &gt; 0>
                  <#assign tags=result.tags />
               <#else>
                  <#assign tags=[] />
               </#if>
               <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
               <!-- Render the tag inputs -->
               <@taglibraryLib.renderTagInputs htmlid=args.htmlid tags=tags tagInputName="tags" />
               <!-- Render the library component -->
               <@taglibraryLib.renderTagLibrary htmlid=args.htmlid site=page.url.templateArgs.site tags=tags />
               <!-- end tags -->
			      <div>
	               <input type="submit" id="${args.htmlid}-save-button" value="Save" />
				      <input type="submit" id="${args.htmlid}-cancel-button" value="Cancel" />
	            </div>
	            </form>
			</div> 
			<div id="#history">
			   <#if result.versionhistory?exists>
			   <table id="versionhistory" style="width: 50%">
			   <tr>
			   <th>Version</th><th>Date</th><th>Author</th>
			   </tr>
			   <#list result.versionhistory as version>
			   <tr><td>${version.version}</td><td>${version.date}</td><td>${version.author}</td><td>(<a href="#" id="${version.versionId}" class="view-link">view</a>)</td></tr>
			   </#list>
			   </table>
			   </#if>
			</div>
	    </div> 
</div>
