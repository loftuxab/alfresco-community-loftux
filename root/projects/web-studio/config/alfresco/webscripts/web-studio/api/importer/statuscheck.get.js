/*
Example URL
http://localhost:8280/studio/service/api/importer?taskId=<taskId>
*/

var taskId = args["taskId"];

// get the importer
var importer = webstudio.importer;

// get the task
var task = importer.getTask(taskId);

if(task != null)
{
	// copy in task properties
	model.hasTask = true;
	model.id = task.id;
	model.name = task.name;
	model.description = task.description;
	model.progressSize = task.progressSize;
	model.progress = task.progress;
	model.status = task.status;
	model.isError = task.isError();
	model.isSuccess = task.isSuccess();
	model.isFinished = task.isFinished();
	model.isRunning = task.isRunning();
	model.isCancelled = task.isCancelled();
}
