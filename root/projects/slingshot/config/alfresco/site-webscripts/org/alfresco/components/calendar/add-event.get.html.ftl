<!-- Add Event Panel -->
<div class="hd">Add event</div>
<div class="bd">

   <form id="${args.htmlid}-addEvent-form" action="${url.context}/proxy/alfresco/calendar/create" method="POST">
        <input type="hidden" name="id" value="bd8ace13-1d07-11dd-b77e-7720ead70151" />
		<input type="hidden" name="tt" value="12:00" />
		<input type="hidden" name="ft" value="12:00" />
      <div class="yui-t1">

            <div class="yui-g">
               <h2>Details</h2>
               <hr/>
            </div>
            <div class="yui-gd">
               <div class="yui-u first">What:</div>
               <div class="yui-u"><input id="${args.htmlid}-title" type="text" name="what"/></div>
            </div>
			<div class="yui-gd">
				<div class="yui-u first">Where:</div>
				<div class="yui-u"><input id="${args.htmlid}-location" type="text" name="where"/></div>
			</div>
            <div class="yui-gd">
               <div class="yui-u first">Description:</div>
               <div class="yui-u"><textarea id="${args.htmlid}-description" name="desc" rows="3" cols="20"></textarea></div>
            </div>
			<div class="yui-g">
	               <h2>Time</h2>
	               <hr/>
	        </div>
			<div class="yui-gd">
				<div class="yui-u first">Start date:</div>
				<div class="yui-u" id="${args.htmlid}-startdate"><input id="fd" type="text" name="fd"/></div>
			</div>
			<div class="yui-gd">
				<div class="yui-u first">End date:</div>
				<div class="yui-u" id="${args.htmlid}-enddate"><input id="td" type="text" name="td"/></div>
			</div>
         	<div class="yui-g">
	            <input type="submit" id="${args.htmlid}-ok-button" value="OK" />
				<input type="submit" id="${args.htmlid}-cancel-button" value="Cancel" />
	        </div>
      </div>

   </form>


</div>
