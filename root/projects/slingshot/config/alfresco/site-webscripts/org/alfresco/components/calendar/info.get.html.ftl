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
               <div class="yui-u">${result.name!""}</div>
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
				<div class="yui-u" id="${args.htmlid}-startdate">${result.from!""}</div>
			</div>
			<div class="yui-gd">
				<div class="yui-u first">End date:</div>
				<div class="yui-u" id="${args.htmlid}-enddate">${result.to!""}</div>
			</div>
			</div>
         	<div class="yui-g">
	            <input type="submit" id="${args.htmlid}-ok-button" value="OK" />
				<input type="submit" id="${args.htmlid}-delete-button" value="Delete" />
	        </div>
      </div>
</div>