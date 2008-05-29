var conf = new XML(config.script);
for each (var url in conf..url)
{
	logger.log("FEED URL: " + url);
}

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

/* TODO: do this in Freemarker */
var timeLabels = [];
for (var i=0; i<24; i++)
{
	var hour = i;
	if (hour < 10)
	{
		hour = "0" + hour;
	}
	timeLabels[i*2] = hour + ":00";
	timeLabels[i*2+1] = hour + ":30";
}

model.timeLabels = timeLabels;