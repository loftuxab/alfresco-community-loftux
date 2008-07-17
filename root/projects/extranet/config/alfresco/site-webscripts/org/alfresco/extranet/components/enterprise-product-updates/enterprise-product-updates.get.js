// component properties
var filterId = instance.properties["filter-id"];
if(filterId == null)
{
	//filterId = "10422";
	filterId = "10430";
}
model.filterId = filterId;

var objects = extranet.getCheckIns(filterId, 0, 8);
model.objects = objects;

var totalObjects = extranet.getCheckIns(filterId, 0, 1000);
model.totalCount = totalObjects.length;