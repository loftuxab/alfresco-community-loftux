var objectTypeId = args["type"];
var objectId = args["id"];
var config = args["config"];

logger.log(config);

var object = sitedata.getObject(objectTypeId, objectId);
if(object == null)
{
	object = sitedata.newObject(objectTypeId, objectId);
}
if(object != null)
{
	// set config onto the object
	object.properties["config"] = config;
	
	// save
	object.save();
}
model.result = object;
