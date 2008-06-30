
function main()
{
    var json = '{ "tags": [ ';
    json += '  	   { "name": "ECM",';
    json += '  	   "count": 5 },';
    json += '  	   { "name": "Alfresco",';
    json += '  	   "count": 10 },';
    json += '  	   { "name": "DMS",';
    json += '  	   "count": 1 } ] }';
    
    var data = eval('(' + json + ')');
    model.tags = data.tags;
    
    /* 
    Look here for the logic about how to create tag cloud
    http://www.petefreitag.com/item/396.cfm
    */
}

main();
