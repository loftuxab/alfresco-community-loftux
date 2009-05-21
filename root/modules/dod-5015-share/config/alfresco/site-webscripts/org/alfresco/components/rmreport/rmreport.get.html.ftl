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
                     FREEZE<br/>
                     HOLD<br/>
                     PERMANENT<br/>
                     REVIEW
                  </div>
               </div>
            </td>
            <td>
               <div class="approved">
                  <span class="header">${msg("label.approved")}</span>
                  <div>
                     FROM: [date]<br/>
                     TO: [date]<br/>
                  </div>
               </div>
            </td>
            <td>
               <div class="reviewed">
                  <span class="header">${msg("label.reviewed")}</span>
                  <div>
                     FROM: [date]<br/>
                     TO: [date]<br/>
                  </div>
               </div>
            </td>
         </tr>
         <tr>
            <td colspan="3">
               <div class="create-report">
                  CREATE REPORT
               </div>
            </td>
         </tr>
      </table>
   </div>
   
   <div id="${el}-results" class="results"></div>
</div>