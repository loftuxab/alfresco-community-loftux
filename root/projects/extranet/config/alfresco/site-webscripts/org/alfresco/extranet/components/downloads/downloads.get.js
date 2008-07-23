// values that have been selected as filters
model.filterClass = args["filterClass"];
model.filterVersion = args["filterVersion"];
model.filterPlatform = args["filterPlatform"];



// create the connector
var connector = remote.connect("alfresco");


// get the values for release class
var productClassesJson = connector.call("/webframework/categories/metadata?path=/Releases/ReleaseClass");
var productClassesObj = eval('(' + productClassesJson + ')');
model.productClasses = productClassesObj.children;

// get the values for release version
var versionsJson = connector.call("/webframework/categories/metadata?path=/Releases/ReleaseVersion");
var versionsObj = eval('(' + versionsJson + ')');
model.versions = versionsObj.children;

// get the values for release platform
var platformsJson = connector.call("/webframework/categories/metadata?path=/Releases/ReleasePlatform");
var platformsObj = eval('(' + platformsJson + ')');
model.platforms = platformsObj.children;


