<script type="text/javascript">//<![CDATA[
   new Alfresco.RecordsReport("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(
      ${messages}
   );
//]]></script>

<#assign el=args.htmlid>
<div id="${el}-body" class="report">
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
               <div class="approved">
                  <span class="header">${msg("label.approved")}</span>
                  <div class="date">
                     <div class="datelabel">${msg("label.from")}:</div>
                     <div class="dateview">
                        <div id="${el}-approved-from" class="calendar"></div>
                        <input id="${el}-approved-from-date" type="text" readonly="readonly" disabled />
                        <img id="${el}-approved-from-toggle" src="${url.context}/components/rmreport/images/calendar-16.png" alt="" />
                     </div>
                     <div class="datelabel">${msg("label.to")}:</div>
                     <div class="dateview">
                        <div id="${el}-approved-to" class="calendar"></div>
                        <input id="${el}-approved-to-date" type="text" readonly="readonly" disabled />
                        <img id="${el}-approved-to-toggle" src="${url.context}/components/rmreport/images/calendar-16.png" alt="" />
                     </div>
                  </div>
               </div>
            </td>
            <td>
               <div class="reviewed">
                  <span class="header">${msg("label.reviewed")}</span>
                  <div class="date">
                     <div class="datelabel">${msg("label.from")}:</div>
                     <div class="dateview">
                        <div id="${el}-reviewed-from" class="calendar"></div>
                        <input id="${el}-reviewed-from-date" type="text" readonly="readonly" disabled />
                        <img id="${el}-reviewed-from-toggle" src="${url.context}/components/rmreport/images/calendar-16.png" alt="" />
                     </div>
                     <div class="datelabel">${msg("label.to")}:</div>
                     <div class="dateview">
                        <div id="${el}-reviewed-to" class="calendar"></div>
                        <input id="${el}-reviewed-to-date" type="text" readonly="readonly" disabled />
                        <img id="${el}-reviewed-to-toggle" src="${url.context}/components/rmreport/images/calendar-16.png" alt="" />
                     </div>
                  </div>
               </div>
            </td>
         </tr>
         <tr>
            <td colspan="3">
               <div class="create-report">
                  <div class="create-report-button">
                     <span class="yui-button yui-push-button" id="${el}-create-report-button">
                        <span class="first-child"><button>${msg("button.createReport")}</button></span>
                     </span>
                  </div>
               </div>
            </td>
         </tr>
      </table>
      <#include "../rmresults-common/rmoptions.ftl" />
   </div>
   
   <div id="${el}-summary" class="summary hidden">
      <div class="summary-buttons">
         <span class="export-report-button">
            <span class="yui-button yui-push-button" id="${el}-export-report-button">
               <span class="first-child"><button>${msg("button.exportReport")}</button></span>
            </span>
         </span>
         <span class="print-report-button">
            <span class="yui-button yui-push-button" id="${el}-print-report-button">
               <span class="first-child"><button>${msg("button.printReport")}</button></span>
            </span>
         </span>
      </div>
      <div id="${el}-summary-title" class="summary-title"></div>
   </div>
   
   <div id="${el}-results" class="results hidden"></div>
</div>