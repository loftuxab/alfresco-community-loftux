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
   <div class="title">${msg("label.searchtitle")}</div>
   
   <div class="builder">
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
   
   <div id="${el}-results" class="results"></div>
</div>