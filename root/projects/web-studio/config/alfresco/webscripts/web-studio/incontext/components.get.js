<import resource="/include/support.js">

/*

http://localhost:8280/studio/proxy/alfresco-web-studio/service/incontext/components?json={%22operation%22%3A%22bindComponent%22%2C%22binding%22%3A{%22componentType%22%3A%22/component/common/image%22%2C%22regionId%22%3A%22uzi%22%2C%22regionSourceId%22%3A%22global%22%2C%22regionScopeId%22%3A%22global%22}%2C%22properties%22%3A{%22title%22%3A%22Image%20Component%22%2C%22description%22%3A%22/images/DSC05618.JPG%22}%2C%22resources%22%3A{%22source%22%3A{%22type%22%3A%22webapp%22%2C%22value%22%3A%22/images/DSC05618.JPG%22}}}&alfStoreId=project1&alfPath=/www/avm_webapps/ROOT

{
	"operation" : "bindComponent"
	,
	"binding" : {

		"componentType" : "image"
		,
		"regionId" : "regionId"
		,
		"regionScopeId" : "regionScopeId"
		,
		"regionSourceId" : "regionSourceId"
	
	}
	,
	"properties" : {
	
		"abc" : "1"
		,
		"def" : "2"
		
	}
	,
	"resources" : {
	
		"resourceId1" : {
		
			"type" : "webapp"
			,
			"value" : "/a/b/c.gif"
			,
			"site" : "site1"

		}
		,
		"resourceId2" : {
		
			"type" : "space"
			,
			"value" : "workspace://SpacesStore/etc"

		}
	}
}

*/

function pushComponentProperties(component, properties)
{
	if(properties != null)
	{
		for(var name in properties)
		{
			var value = properties[name];
			
			var proceed = true;
			
			if(typeof value == 'function')
			{
				proceed = false;
			}
			
			if(name == 'toJSONString')
			{
				proceed = false;
			}
			
			if(proceed)
			{
				component.setProperty(name, value);
			}
		}
	}
}

function pushComponentResources(component, resources)
{
	if(resources != null)
	{
		for(var resourceId in resources)
		{
			var config = resources[resourceId];
			
			// remove existing resource if there is one
			component.resources.remove(resourceId);
			
			if(resourceId != "toJSONString")
			{		
				// add resource	
				var resource = component.resources.add(resourceId);
	
				// configure resource
				for(var attributeName in config)
				{
					var attributeValue = config[attributeName];
	
					var proceed = true;
	
					if(typeof attributeValue == 'function')
					{
						proceed = false;
					}
					if(attributeName == "prototype")
					{
						proceed = false;
					}
					if(attributeName == "toJSONString")
					{
						proceed = false;
					}
	
					if(proceed)
					{
						if(attributeName == "value")
						{
							resource.value = attributeValue;
						}
						else
						{
							resource.properties[attributeName] = attributeValue;
						}
					}
				}
			}
		}
	}
}

// the json object
var input = eval('(' + args["json"] + ')');

// the operation
var operation = input["operation"];

// binding parameters
var binding = input["binding"];
var componentTypeId = binding["componentType"];
var regionId = binding["regionId"];
var regionSourceId = binding["regionSourceId"];
var regionScopeId = binding["regionScopeId"];

// properties to be set
var properties = input["properties"];

// resources to be set
var resources = input["resources"];

// set up the return json string
var output = { };
if(componentTypeId != null)
{
	output["componentTypeId"] = componentTypeId;
}
output["regionId"] = regionId;
output["regionSourceId"] = regionSourceId;
output["regionScopeId"] = regionScopeId;

// we have to have values for all of these
var proceed = true;
if(componentTypeId == null)
{
	proceed = false;
	output["message"] = "No component type id";
}
if(regionId == null)
{
	proceed = false;
	output["message"] = "No region id";
}
if(regionSourceId == null)
{
	proceed = false;
	output["message"] = "No region source id";
}
if(regionScopeId == null)
{
	proceed = false;
	output["message"] = "No region scope id";
}

// asset that we are in a valid state
if(!proceed)
{
	output["status"] = "fail";
}

var component = null;
if(componentTypeId != null)
{	
	// build the component
	component = sitedata.newComponent(componentTypeId);

	// push component properties
	pushComponentProperties(component, properties);

	// push component resources
	pushComponentResources(component, resources);
	
	// save the component
	component.save();

	// bind the component
	sitedata.bindComponent(component, regionScopeId, regionId, regionSourceId);

	// assign component id onto json return
	output["componentId"] = component.getId();
}

// format the json
var outputString = output.toJSONString();
var callback = args["callback"];
if(callback != null)
{
	outputString = callback + "(" + outputString + ");";
}
model.json = outputString;

