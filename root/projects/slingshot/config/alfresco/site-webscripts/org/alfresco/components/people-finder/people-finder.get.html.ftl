<#assign el=args.htmlid?html>
<script type="text/javascript">//<![CDATA[
   new Alfresco.PeopleFinder("${el}").setOptions(
   {
      siteId: "<#if page?exists>${page.url.templateArgs.site!""}<#else>${(args.site!"")?js_string}</#if>",
      minSearchTermLength: ${(args.minSearchTermLength!config.scoped['Search']['search'].getChildValue('min-search-term-length'))?js_string},
      maxSearchResults: ${(args.maxSearchResults!config.scoped['Search']['search'].getChildValue('max-search-results'))?js_string},
      setFocus: ${(args.setFocus!'false')?js_string},
      addButtonSuffix: "${(args.addButtonSuffix!'')?js_string}",
      dataWebScript: "${(args.dataWebScript!'api/people')?replace("[", "{")?replace("]", "}")?js_string}",
      viewMode: ${args.viewMode!"Alfresco.PeopleFinder.VIEW_MODE_DEFAULT"}
   }).setMessages(
      ${messages}
   );
//]]></script>

<div id="${el}-body" class="people-finder list">
   
   <div class="title"><label for="${el}-search-text">${msg("title")}</label></div>
   
   <div class="finder-wrapper">
      <div class="search-bar theme-bg-color-3">
         <div class="search-text"><input type="text" id="${el}-search-text" name="-" value="" maxlength="256" tabindex="0"/></div>
         <div class="search-button">
            <span id="${el}-search-button" class="yui-button yui-push-button"><span class="first-child"><button>${msg("button.search")}</button></span></span>
         </div>
      </div>
      
      <div id="${el}-help" class="yui-g theme-bg-color-2 help hidden">
         <div class="title">Search Tips</div>
         <div class="yui-u first">
            <div class="subtitle">Search by Name or User Name</div>
            <div class="info">To search for users by first name, last name or user name just type a single term, for example:</div>
            <div class="example">Joe</div>
            <div class="info">The results for this query will list all users whose first name, last name or user name starts with Joe.</div>
            <div class="info">To search for users by first and last name, separate two terms with a space, for example:</div>
            <div class="example">Joe Bloggs</div>
            <div class="info">The results for this query will list all users whose first name starts with Joe and and surname starts with Bloggs.</div>
            <div class="info">You can also enter just the start of the name, for example:</div>
            <div class="example">Joe B</div>
            <div class="info">The results for this query will list all users whose first name starts with Joe and and surname starts with B.</div>
         </div>
         <div class="yui-u">
            <div class="subtitle">Search by Property</div>
            <div class="info">To search for users by property add the property as a prefix, for example:</div>
            <div class="example">location:maidenhead</div>
            <div class="info">The results for this query would find users whose location contains maidenhead.</div>
            <div class="info">The search will AND multiple terms together, for example:</div>
            <div class="example">jobtitle:engineer organization:alfresco</div>
            <div class="info">The results for this query would find users whose job title contains engineer and organization contains alfresco.</div>
            <div class="info">To reduce search results by property, add a property search after a name search, for example:</div>
            <div class="example">Smith jobtitle:engineer</div>
            <div class="info">The results for this query would find users whose first name, last name or user name starts with Smith and job title contains engineer.</div>
            <div class="example">A Smith jobtitle:engineer</div>
            <div class="info">The results for this query will list all users whose first name starts with A and and surname starts Smith and job title contains engineer.</div>
         </div>
      </div>
      
      <div id="${el}-results" class="results hidden"></div>
   </div>
</div>