<script type="text/javascript">//<![CDATA[
   var view = new Alfresco.CalendarView("${args.htmlid}").setSiteId(
   "${page.url.templateArgs.site!""}"
   ).setMessages(
      ${messages}
   );
<#if page.url.args["date"]?exists>
   view.currentDate = new Date("${page.url.args["date"]}");
</#if>
//]]></script>

<div id="${args.htmlid}">
<div id="eventInfoPanel"></div>
<div id="calendar-view">

<div class="yui-content" style="background: #FFFFFF;">
   <div id="${args.htmlid}-day">
      <div id="${args.htmlid}-dayLabel" class="date-title"></div>
      <table width="100%" cellpadding="0" cellspacing="0" border="0">
        <tr><td>
      <div id="dayContainer">
        <div id="timeLabels">
         <#list 0..23 as i>
            <#assign time = i?string>
            <#if i < 10>
               <#assign time = "0" + time>
            </#if>
            <div class="timeLabel<#if i == 23> last</#if>">${time}:00</div>
         </#list>
        </div>
        <div id="${args.htmlid}-dayEventsView" class="dayEventsView"><#-- events go here --></div>
      </div>
      </td></tr>
      </table>                  
   </div>
   <div id="${args.htmlid}-week">
      <div id="${args.htmlid}-weekLabel" class="date-title"></div>
      <table id="week-view" cellspacing="0" cellpadding="2" border="1" width="100%">
      <tr>
      <th></th>
      <#list columnHeaders as header>
         <th id="${args.htmlid}-weekheader-${header_index}" align="center" valign="top"><a href="#">${header?string("E M/d")}</a></th>
      </#list>
      </tr>
      <#assign cellcount = 0  />
      <#list 0..23 as i>
         <#assign time = i?string>
         <#if i < 10>
            <#assign time = "0" + time>
         </#if>
         <#if i % 2 == 0>
            <#assign class="even">
         <#else>
            <#assign class="odd">
         </#if>
         <tr class="${class}">   
         <td class="label">${time}:00</td>
         <#list 1..7 as day>
            <td id="${args.htmlid}_calendar_cell${cellcount}"></td>
            <#assign cellcount = cellcount + 1 />
         </#list>
         </tr>
         <tr class="${class}">
            <td class="label">&nbsp;</td>
            <#list 1..7 as day>
               <td id="${args.htmlid}_calendar_cell${cellcount}"></td>
               <#assign cellcount = cellcount + 1 />
            </#list>
         </tr>   
      </#list>
      </table>
   </div>
   <div id="${args.htmlid}-month">
      <div id="${args.htmlid}-monthLabel" class="date-title"></div>
      <table id="month-view">
      <tr>
      <#assign days_in_week = msg("days.medium")?split(",") >
      <#list days_in_week as day>
         <th align="center" width="14%">${day}</th>
         </#list>
      </tr>
      <#list 0..5 as row><#-- ROW -->
      <tr>
         <#list 0..6 as column><#-- COLUMN -->
            <#assign id = (row?number * 7) + column>
            <td width="14%" id="cal_month_t_${id}">
               <div class="boxOutline">
                  <div id="dh${id}" class="dayLabel"></div>
               </div>
            </td>
            </#list>
      </tr>
      </#list>
      </table>
   </div>
   <div id="${args.htmlid}-agenda">
      <div class="date-title">${msg("title.agenda")}</div>
      <div id="${args.htmlid}-agendaContainer" style="width: 100%;"></div>
   </div>
</div>
</div>
</div>
