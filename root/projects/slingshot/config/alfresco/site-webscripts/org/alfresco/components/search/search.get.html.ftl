<script type="text/javascript">//<![CDATA[
   new Alfresco.Search("${args.htmlid}").setOptions(
   {
      siteId: "${siteId}",
      containerId: "",
      initialSearchTerm: "${searchTerm?html}",
      initialSearchAll: "${searchAll?string}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<#macro resultbar uniqueid cssclass>
   <div class="${cssclass}">
      <span class="search-result-info">
      </span>
      
      <#-- Only add switch if we are in a site context -->
      <#if siteId?has_content>
      <span>
      (
      <a href="#" id="${args.htmlid}-toggleSearchScope-${uniqueid}" class="search-scope-toggle">
      </a>
      )
      </span>
      </#if>
   </div>
</#macro>

<div id="${args.htmlid}-body" class="search">
	<@resultbar uniqueid="first" cssclass="resultbar" />
	
	<#-- this div contains the search results -->
	<div id="${args.htmlid}-results" class="results"></div>
</div>