/* Number of ms in a day */
var DAY = 24*60*60*1000;
/* Days in a week */
var DAYS_IN_WEEK = 7;

var now = new Date();
/* Start at the beginning of the week for the week view */
var startTime = now.getTime() - (DAY * now.getDay());
var startDate = new Date(startTime);

var columnHeaders = [];
for(var i=0; i < DAYS_IN_WEEK; i++)
{
	columnHeaders.push(
		new Date(startDate.getTime())
	);
	startDate.setTime(startDate.getTime() + DAY);
}

model.columnHeaders = columnHeaders;