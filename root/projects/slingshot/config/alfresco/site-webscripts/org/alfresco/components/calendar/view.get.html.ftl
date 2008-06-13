<script type="text/javascript">//<![CDATA[
   var view = new Alfresco.CalendarView("${args.htmlid}").setSiteId(
	"${page.url.args["site"]!""}"
	).setMessages(
		${messages}
	);
<#if page.url.args["date"]?exists>
   view.currentDate = new Date("${page.url.args["date"]}");
</#if>
//]]></script>

<div id="${args.htmlid}">
<div id="calendar-view" class="yui-navset">
<ul class="yui-nav">
  <li><a href="#day"><em>${msg("label.day")}</em></a></li>
  <li><a href="#week"><em>${msg("label.week")}</em></a></li>
  <li class="selected"><a href="#month"><em>${msg("label.month")}</em></a></li>
  <li><a href="#agenda"><em>${msg("label.agenda")}</em></a></li>
</ul>

<div class="yui-content" style="background: #FFFFFF;">
	<div id="#day">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr><td align="center" style="font-weight: bold;"><#-- default date goes here --></td></tr>
		</table>
		<br />
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
		  <tr><td>
		<div id="dayContainer">
		  <div id="timeLabels">
			<#list 0..23 as i>
				<#assign time = i?string>
				<#if i < 10>
					<#assign time = "0" + time>
				</#if>
				<div class="timeLabel">${time}:00</div>
				<div class="timeLabel">${time}:30</div>
			</#list>
		  </div>
		  <div id="${args.htmlid}-dayEventsView" class="dayEventsView"><#-- events go here --></div>
		</div>
		</td></tr>
		</table>						
	</div>
	<div id="#week">
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
		        <tr><td align="center" style="font-weight: bold;">11 - 17 May 2008</td></tr>
		</table>
		<br/>
		<table id="week-view" cellspacing="0" cellpadding="2" border="1" width="100%">
		<tr>
		<th></th>
		<#list columnHeaders as header>
			<th align="center" valign="top"><a href="#">${header?string("E M/d")}</a></th>
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
			<tr class="<#if class == "even">odd<#else>even</#if>">
				<td class="label">${time}:30</td>
				<#list 1..7 as day>
					<td id="${args.htmlid}_calendar_cell${cellcount}"></td>
					<#assign cellcount = cellcount + 1 />
				</#list>
			</tr>	
		</#list>
		</table>
	</div>
	<div id="#month">
		<div style="text-align:center">
			<a href="#" id="${args.htmlid}-prev-button">${msg("button.previous")}</a><a href="#" id="${args.htmlid}-current-button">${msg("button.current_month")}</a><a href="#" id="${args.htmlid}-next-button">${msg("button.next")}</a> 
		</div>
		<div id="monthLabel">May 2008</div>
		<br/>
		<table id="month-view">
		<tr>
		<#assign days_in_week = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]>
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
	<div id="#agenda">
		<div id="${args.htmlid}-agendaContainer" style="width: 100%;"></div>
	</div>
</div>
</div>
</div>
