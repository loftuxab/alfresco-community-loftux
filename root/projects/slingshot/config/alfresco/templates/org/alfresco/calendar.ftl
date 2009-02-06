<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/templates/calendar/calendar.css" />
</@>

<@templateBody>
   <div id="hd">
      <@region id="header" scope="global" protected=true />
      <@region id="title" scope="template" protected=true />
      <@region id="navigation" scope="template" protected=true />
   </div>
   <div id="bd">
        <div class="yui-t1" id="divCalendarWrapper">
           <div id="yui-main">
              <div id="divCalendarEvents">
                <@region id="toolbar" scope="template" protected=true />               
                <@region id="view" scope="template" protected=true />
              </div>
           </div>
           <div id="divCalendarFilters">
               <@region id="calendar" scope="template" protected=true />
               <@region id="tags" scope="template" protected=true />
           </div>
        </div>
   </div>
</@>

<@templateFooter>
   <div id="ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>

