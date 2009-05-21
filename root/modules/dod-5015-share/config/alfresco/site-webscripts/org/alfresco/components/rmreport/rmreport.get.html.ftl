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
               <div class="designation">
                  <span class="header">${msg("label.designation")}</span>
                  <div>
                     <ul>
                        <li>
                           <input type="checkbox" id="${el}-designation-freeze" />
                           <label for="${el}-designation-freeze">${msg("label.freeze")}</label>
                        </li>
                        <li>
                           <input type="checkbox" id="${el}-designation-hold" />
                           <label for="${el}-designation-hold">${msg("label.hold")}</label>
                        </li>
                        <li>
                           <input type="checkbox" id="${el}-designation-permanent" />
                           <label for="${el}-designation-permanent">${msg("label.permanent")}</label>
                        </li>
                        <li>
                           <input type="checkbox" id="${el}-designation-review" />
                           <label for="${el}-designation-review">${msg("label.review")}</label>
                        </li>
                     </ul>
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
                     <span class="yui-button yui-push-button" id="${el}-report-button">
                        <span class="first-child"><button>${msg("button.createReport")}</button></span>
                     </span>
                  </div>
               </div>
            </td>
         </tr>
      </table>
   </div>
   
   <div id="${el}-results" class="results"></div>
</div>