<!-- Event Info Panel -->
<div class="hd">Event Info</div>
<div class="bd">
      <div class="yui-t1">
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
			<div class="yui-gd">
				<div class="yui-u first">Start date:</div>
				<div class="yui-u" id="${args.htmlid}-startdate"><#if result.from?exists>${result.from?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if></div>
			</div>
			<div class="yui-gd">
				<div class="yui-u first">Start time:</div>
				<div class="yui-u">${result.start!""}</div>
			</div>
			<div class="yui-gd">
				<div class="yui-u first">End date:</div>
				<div class="yui-u" id="${args.htmlid}-enddate"><#if result.to?exists>${result.to?date("MM/dd/yyy")?string("EEEE, MMMM dd yyyy")}</#if></div>
			</div>
			<div class="yui-gd">
				<div class="yui-u first">End time:</div>
				<div class="yui-u">${result.end!""}</div>
			</div>
			</div>
         	<div class="yui-g">
	            <input type="submit" id="${args.htmlid}-edit-button" value="Edit" />
				<input type="submit" id="${args.htmlid}-delete-button" value="Delete" />
				<input type="submit" id="${args.htmlid}-cancel-button" value="Cancel" />
	        </div>
      </div>
</div>