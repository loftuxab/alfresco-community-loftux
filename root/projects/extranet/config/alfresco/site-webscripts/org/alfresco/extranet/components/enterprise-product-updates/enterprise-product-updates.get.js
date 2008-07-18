// component properties
var filterId = instance.properties["filter-id"];
if(filterId == null)
{
	filterId = "10430";
}
model.filterId = filterId;
model.totalCount = 0;

var objects = extranet.getCheckIns(filterId, 0, 8);
model.objects = objects;

if(objects != null)
{
	var totalObjects = extranet.getCheckIns(filterId, 0, 1000);
	if(totalObjects != null)
	{
		model.totalCount = totalObjects.length;
	}
}
else
{
	model.errorMessage = "Unable to contact server";
}