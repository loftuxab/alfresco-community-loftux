<#if hasTask?exists>

{
	"id" : "${id}"
	,
	"name" : "${name}"
	,
	"description" : "${description}"
	,
	"progressSize" : ${progressSize}
	,
	"progress" : ${progress}
	,
	"status" : "${status}"
	,
	"isError" : ${isError?string}
	,
	"isSuccess" : ${isSuccess?string}
	,
	"isFinished" : ${isFinished?string}
	,
	"isRunning" : ${isRunning?string}
	,
	"isCancelled" : ${isCancelled?string}
}
</#if>