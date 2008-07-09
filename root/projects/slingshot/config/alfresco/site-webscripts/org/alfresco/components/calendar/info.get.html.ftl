<!-- Event Info Panel -->
<div class="hd">Event Info</div>
<div class="bd">
      <div class="yui-t1" style="width: 500px; min-width: 500px">
            <div class="yui-g">
               <h2>Details</h2>
               <hr/>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">What: *</div>
               <div class="yui-u">${result.what!""}</div>
            </div>
			<div class="yui-gd">
				<div class="yui-u first">Where:</div>
				<div class="yui-u">${result.location!""}</div>
			</div>
            <div class="yui-gd">
               <div class="yui-u first">Description:</div>
               <div class="yui-u">${result.description!""}</div>
            </div>
			<div class="yui-g">
	               <h2>Time</h2>
	               <hr/>
	        </div>
	        <#if result.allday?exists>
	        <div class="yui-gd">
	         <div class="yui-u first">&nbsp;</div>
	         <div class="yui-u"><em>This is an all day event</em></div>
	        </div>
	        </#if>
           <div class="yui-gd">
            <div class="yui-u first">Start date:</div>
				<div class="yui-u" id="${args.htmlid}-startdate"><#if result.from?exists>${result.from?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if><#if !result.allday?exists> at ${result.start!""}</#if></div>
			</div>
			<div class="yui-gd">
				<div class="yui-u first">End date:</div>
				<div class="yui-u" id="${args.htmlid}-enddate"><#if result.to?exists>${result.to?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if><#if !result.allday?exists> at ${result.end!""}</#if></div>
			</div>
			</div>
         	<div class="yui-g">
	            <input type="submit" id="${args.htmlid}-edit-button" value="Edit" />
				   <input type="submit" id="${args.htmlid}-delete-button" value="Delete" />
				   <input type="submit" id="${args.htmlid}-cancel-button" value="Cancel" />
	        </div>
      </div>
</div>