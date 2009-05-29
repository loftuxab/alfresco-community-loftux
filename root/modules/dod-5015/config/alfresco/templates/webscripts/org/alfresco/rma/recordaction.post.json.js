function main()
{
    // check that required data is present in request body
    if (json.has("nodeRef") === false)
    {
        status.setCode(status.STATUS_BAD_REQUEST, "nodeRef parameter is not present");
        return;
    }
    
    if (json.has("name") === false)
    {
        status.setCode(status.STATUS_BAD_REQUEST, "name parameter is not present");
        return;
    }
    
    // extract required data from request body
    var nodeRef = json.get("nodeRef");
    var actionName = json.get("name");

    if (logger.isLoggingEnabled())
    {
    	logger.log("nodeRef = " + nodeRef);
    	logger.log("name = " + name);
    }

    // extract optional data from request body (if present)
    //TODO Are the params optional or mandatory?
    var params = null; 
    if (json.has("params"))
    {
    	// convert the JSON object into a native JavaScript array
    	params = json.get("params");
    	
    	if (logger.isLoggingEnabled())
    		logger.log("params = " + params);
    }
    
    try
    {
    	rmService.executeRecordAction(nodeRef, actionName, params);
    }
    catch (error)
    {
    	var msg = error.message;
    	if (logger.isLoggingEnabled())
    		logger.log(msg);

    	if (error.javaException instanceof Packages.org.alfresco.service.cmr.repository.InvalidNodeRefException) {
    		if (logger.isLoggingEnabled())
    			logger.log("Returning 404 status code");

    		status.setCode(404, msg);
            return;
	    }
    	else {
    		if (logger.isLoggingEnabled())
    			logger.log("Returning 500 status code");

    		status.setCode(500, msg);

            return;
        }
    }

    model.message = "Successfully queued action [" + actionName + "] on " + nodeRef;
}

main();
