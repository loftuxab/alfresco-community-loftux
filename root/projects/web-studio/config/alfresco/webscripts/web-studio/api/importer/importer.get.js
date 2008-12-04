/*
Example URL
http://localhost:8280/studio/service/api/importer?alfStoreId=project3&url=/resources/green.zip
*/

var url = args["url"];
var store = args["alfStoreId"];

// get the importer
var importer = webstudio.importer;

// import the archive
var taskId = importer.importArchive(store, "ROOT", url);

model.status = "ok";
model.message = "The archive import was started.";
model.taskId = taskId;