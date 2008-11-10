var objectTypeId = args["type"];
var objectId = args["id"];

var object = sitedata.getObject(objectTypeId, objectId);
model.result = object;
