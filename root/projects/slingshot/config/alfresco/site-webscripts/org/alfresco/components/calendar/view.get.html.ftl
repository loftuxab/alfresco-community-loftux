<script type="text/javascript">//<![CDATA[
   var calendarView = new Alfresco.CalendarView("${args.htmlid}");
//]]></script>

<div id="calendar-view" class="yui-navset" style="height: 500px; overflow-x: hidden; overflow-y: scroll; border: 1px solid black;"">
<ul class="yui-nav">
  <li><a href="#day"><em>Day</em></a></li>
  <li><a href="#week"><em>Week</em></a></li>
  <li class="selected"><a href="#month"><em>Month</em></a></li>
  <li><a href="#agenda"><em>Agenda</em></a></li>
</ul>
<div class="yui-content" style="background: #FFFFFF;">
	<div id="#day">
	 <div style="text-align:center">
	         <img src="/slingshot/images/calendar/prevMinor.gif" title="Previous Day" style="cursor:pointer" />
	         <img src="/slingshot/images/calendar/now.gif" title="Today" style="cursor:pointer" />
	         <img src="/slingshot/images/calendar/nextMinor.gif" title="Next Day" style="cursor:pointer" />
	    </div>
		<table width="100%" cellpadding="0" cellspacing="0" border="0">
			<tr><td align="center" style="font-weight: bold;">17 May 2008</td></tr>
		</table>
		<table id="day-view" cellspacing="0" cellpadding="2" border="1" width="100%">
		<#assign counter=0>
		<#list timeLabels as time>
			<#if counter % 2 == 0>
				<tr class="even">
			<#else>
				<tr class="odd">
			</#if>
			<td class="label">${time}</td>
			<td></td>
			</tr>
			<#assign counter = counter + 1>
		</#list>
		</table>
	</div>
	<div id="#week">
	    <div style="text-align:center">
	         <img src="/slingshot/images/calendar/prevMinor.gif" title="Previous Week" style="cursor:pointer" />
	         <img src="/slingshot/images/calendar/now.gif" title="This Week" style="cursor:pointer" />
	         <img src="/slingshot/images/calendar/nextMinor.gif" title="Next Week" style="cursor:pointer" />
	    </div>
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
		<#assign counter=0>
		<#list timeLabels as time>
			<#if counter % 2 == 0>
				<tr class="even">
			<#else>
				<tr class="odd">
			</#if>
			<td class="label">${time}</td>
			<#list 1..7 as day>
				<td></td>
			</#list>	
			</tr>
			<#assign counter = counter + 1>
		</#list>
		</table>
	</div>
	<div id="#month">
		<div style="text-align:center">
			<a href="#" id="${args.htmlid}-prev-button">Previous</a><a href="#" id="${args.htmlid}-current-button">This Month</a><a href="#" id="${args.htmlid}-next-button">Next</a> 
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
						<div id="dh${id}" class="dayLabel"><a href="?${id}">${id}</a></div>
					</div>
				</td>
				</#list>
		</tr>
		</#list>
		</table>
	</div>
	<div id="#agenda">
	Nothing to see here.
	</div>
</div>
</div>