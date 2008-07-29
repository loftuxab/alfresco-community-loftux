var connector = remote.connect("alfresco");

//
// News Items
//
model.maxNewsCount = 3;
var newsItems = connector.get("/network/news/retrieval");
var obj1 = eval('(' + newsItems + ')');
model.newsItems = obj1.results;


//
// Assets
//
model.maxAssetCount = 3;
var assets = connector.get("/network/library/retrieval?count=" + model.maxAssetCount);
var obj2 = eval('(' + assets + ')');
model.assets = obj2.results;



