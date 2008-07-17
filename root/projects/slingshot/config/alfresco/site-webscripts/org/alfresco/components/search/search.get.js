model.siteId = (args["site"] != undefined) ? args["site"] : "";
model.containerId = (args["container"] != undefined) ? args["container"] : "";
model.searchTerm = (args["searchTerm"] != undefined) ? args["searchTerm"] : "";
model.searchAll = (args["searchAll"] != undefined) ? parseBoolean(args["searchAll"]) : true;
