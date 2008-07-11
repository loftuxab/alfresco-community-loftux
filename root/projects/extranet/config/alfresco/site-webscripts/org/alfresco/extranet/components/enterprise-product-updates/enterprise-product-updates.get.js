// component properties
var endpoint = instance.properties["endpoint"];
var category = instance.properties["category"];

// connect and retrieve json
var connector = remote.connect("alfresco");
var str = connector.get("/network/enterprise-product-updates?category="+category);

var json = eval('(' + str + ')');

model.objects = json.results;