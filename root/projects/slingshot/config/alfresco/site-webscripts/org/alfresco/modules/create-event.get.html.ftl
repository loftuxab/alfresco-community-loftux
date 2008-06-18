<!-- Add Event Panel -->
<div class="hd">Add event</div>
<div class="bd">

   <form id="${args.htmlid}-addEvent-form" action="${url.context}/proxy/alfresco/calendar/create" method="POST">
      <div class="yui-t1" style="width: 500px; min-width: 500px">
			<input type="hidden" name="site" value="${args["site"]!""}" />
			<input type="hidden" id="from" name="from" value="${event.from!""}" />
			<input type="hidden" id="to" name="to" value="${event.to!""}" />

            <div class="yui-g">
               <h2>Details</h2>
               <hr/>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">What: *</div>
               <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="what" value="${event.what!""}"/></div>
            </div>
			<div class="yui-gd">
				<div class="yui-u first">Where:</div>
				<div class="yui-u"><input id="${args.htmlid}-location" type="text" name="where" value="${event.location!""}"/></div>
			</div>
            <div class="yui-gd">
               <div class="yui-u first">Description:</div>
               <div class="yui-u"><textarea id="${args.htmlid}-description" name="desc" rows="3" cols="20">${event.description!""}</textarea></div>
            </div>
			<div class="yui-g">
	               <h2>Time</h2>
	               <hr/>
	        </div>
			<div class="yui-gd">
				<div class="yui-u first">Start date:</div>
				<div class="yui-u" id="${args.htmlid}-startdate"><input id="fd" type="text" name="datefrom" readonly="readonly" value="<#if event.from?exists>${event.from?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if>"/></div>
			</div>
			<div class="yui-gd">
				<div class="yui-u first">&nbsp;</div>
				<div class="yui-u">&nbsp;at&nbsp;<input id="${args.htmlid}-start" name="start" value="${event.start!"12:00"}" type="text" size="10" /></div>
			</div>
			<div class="yui-gd">
				<div class="yui-u first">End date:</div>
				<div class="yui-u" id="${args.htmlid}-enddate"><input id="td" type="text" name="dateto" readonly="readonly" value="<#if event.to?exists>${event.to?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if>"/></div>
			</div>
			<div class="yui-gd">
				<div class="yui-u first">&nbsp;</div>
				<div class="yui-u">&nbsp;at&nbsp;<input id="${args.htmlid}-end" name="end" value="${event.end!"13:00"}" type="text" size="10" /></div>
			</div>
         	<div class="yui-g">
	            <input type="submit" id="${args.htmlid}-ok-button" value="OK" />
				<input type="submit" id="${args.htmlid}-cancel-button" value="Cancel" />
	        </div>
      </div>

   </form>


</div>