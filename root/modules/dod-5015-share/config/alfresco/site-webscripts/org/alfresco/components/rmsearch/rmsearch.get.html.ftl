<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsSearch("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="search">
   <div class="yui-g">
      <div class="yui-u first">
         <div class="title">${msg("label.searchtitle")}</div>
      </div>
      <div class="yui-u topmargin">
         <!-- New Search button -->
         <div class="newsearch-button">
            <span class="yui-button yui-push-button" id="${el}-newsearch-button">
               <span class="first-child"><button>${msg("button.newsearch")}</button></span>
            </span>
         </div>
         
         <!-- Save Search button -->
         <div class="savesearch-button">
            <span class="yui-button yui-push-button" id="${el}-savesearch-button">
               <span class="first-child"><button>${msg("button.savesearch")}</button></span>
            </span>
         </div>
         
         <!-- Saved Searches menu button -->
         <div class="savedsearches-button">
            <span class="yui-button yui-push-button" id="${el}-savedsearches-button">
               <span class="first-child"><button>${msg("button.savedsearches")}</button></span>
            </span>
         </div>
      </div>
   </div>
   
   <div id="${el}-tabs" class="yui-navset">
      <ul class="yui-nav">
         <li class="selected"><a href="#${el}-critera-tab"><em>${msg("label.criteria")}</em></a></li>
         <li><a href="#${el}-results-tab"><em>${msg("label.results")}</em></a></li>
      </ul>            
      <div class="yui-content tab-content">
         <div id="${el}-critera-tab">
            <table cellspacing="0" cellpadding="0" border="0">
               <tr>
                  <td>
                     <div class="records">
                        <span class="header">${msg("label.records")}</span>
                        <div>
                           <input type="checkbox" id="${el}-undeclared" />
                           <label for="${el}-undeclared">${msg("label.undeclared")}</label>
                        </div>
                     </div>
                  </td>
                  <td>
                     <div class="terms">
                        <span class="header">${msg("label.searchterm")}</span>
                        <div class="query">
                           <!-- Query text input -->
                           <textarea id="${el}-query" rows="2" cols="40"></textarea>
                        </div>
                     </div>
                  </td>
               </tr>
               <tr>
                  <td colspan="2">
                     <div class="execute-search">
                        <div class="search-button">
                           <span class="yui-button yui-push-button" id="${el}-search-button">
                              <span class="first-child"><button>${msg("button.search")}</button></span>
                           </span>
                        </div>
                     </div>
                  </td>
               </tr>
            </table>
            <#include "../rmresults-common/rmoptions.ftl" />
         </div>
         
         <div id="${el}-results-tab">
            <div class="yui-g">
               <div class="yui-u first">
                  <span id="${el}-itemcount"></span>
               </div>
               <div class="yui-u alignright">
                  <span class="print-button">
                     <span class="yui-button yui-push-button" id="${el}-print-button">
                        <span class="first-child"><button>${msg("button.print")}</button></span>
                     </span>
                  </span>
                  <span class="export-button">
                     <span class="yui-button yui-push-button" id="${el}-export-button">
                        <span class="first-child"><button>${msg("button.export")}</button></span>
                     </span>
                  </span>
               </div>
            </div>
            <!-- results inserted here into YUI Datagrid -->
            <div id="${el}-results" class="results"></div>
         </div>
      </div>
   </div>
</div>