var json = '{ "tags": [ ';
json += '  	   { "name": "Brand",';
json += '  	   "count": 3 },';
json += '  	   { "name": "Compute",';
json += '  	   "count": 5 },';
json += '  	   { "name": "Desogm",';
json += '  	   "count": 6 } ] }';

var data = eval('(' + json + ')');
model.tags = data.tags;

/* 
Look here for the logic about how to create tag cloud
http://www.petefreitag.com/item/396.cfm
*/