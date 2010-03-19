<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <script type="text/javascript">//<![CDATA[
   (function()
   {
      // If no location.hash exists, convert certain params in location.search to location.hash and replace the page
      var loc = window.location;
      if (loc.hash === "" && loc.search !== "")
      {
         var qs = Alfresco.util.getQueryStringParameters(), q, url = loc.protocol + "//" + loc.host + loc.pathname, hash = "";
         var hashParams =
         {
            "page": true,
            "filter": true
         },
            filterDataParam = "filterData";

         for (q in qs)
         {
            if (qs.hasOwnProperty(q) && q in hashParams)
            {
               hash += "&" + q + "=" + encodeURIComponent(qs[q]);
               if (q === "filter")
               {
                  // Check for filterData in QueryString for the "filter" case
                  if (qs.hasOwnProperty(filterDataParam))
                  {
                     hash += "|" + encodeURIComponent(qs[filterDataParam]);
                     delete qs[filterDataParam];
                  }
               }
               delete qs[q];
            }
         }
         
         if (hash.length > 0)
         {
            url += Alfresco.util.toQueryString(qs) + "#" + hash.substring(1);
            window.location.replace(url);
         }
      }
   })();
   //]]></script>
   <@script type="text/javascript" src="${url.context}/js/alfresco-resizer.js"></@script>
   <script type="text/javascript">//<![CDATA[
      new Alfresco.widget.Resizer();
   //]]></script>
   <@script type="text/javascript" src="${url.context}/modules/data-lists/datalist-actions.js"></@script>
</@>

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
      <@region id="actions-common" scope="template" protected=true />
        <div class="yui-t1">
           <div id="yui-main">
              <div class="yui-b" id="divRight">
                <@region id="toolbar" scope="template" protected=true />               
                <@region id="datagrid" scope="template" protected=true />
              </div>
           </div>
           <div class="yui-b" id="divLeft">
               <@region id="datalists" scope="template" protected=true />
               <@region id="filter" scope="template" protected=true />
               <@region id="REMOVED-tags" scope="template" protected=true />
           </div>
        </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
