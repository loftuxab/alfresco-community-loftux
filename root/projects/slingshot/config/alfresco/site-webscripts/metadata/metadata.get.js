/*

// get the details of the node showing metadata for i.e. type, property config etc.

// TODO: use remote object to fetch DD info.

// create an object representing type info (this would be result of DD webscript call)
var nodeDef = 
{
   type: "cm:content",
   "cm:name": {type: "d:text", multivalued: false, editable: true, readonly: true},
   "app:title": {type: "d:text", multivalued: false, editable: true, readonly: false},
   "app:description": {type: "d:text", multivalued: false, editable: true, readonly: false}
};

// TODO: use a remote object to fetch node properties

// create an object to represent node properties
var nodeProps = 
{
   "cm:name": "somefile.txt",
   "app:title": "Some File",
   "app:description": "This is a description for the file"
};

// retrieve global config for controls
var controls = config.global["metadata-controls"];

// retrieve scoped metadata config for the type
var itemsToShow = config.scoped[nodeDef.type].metadata;

// augment node definition with local config
var children = itemsToShow.children;
var items = [];
for (var i = 0; i < children.size(); i++)
{
   var itemName = children.get(i).attributes["name"];
   items[items.length] = itemName;
   var itemLabel = children.get(i).attributes["label"];
   nodeDef[itemName].label = itemLabel;
   var itemType = nodeDef[itemName].type;
   
   var itemControl = children.get(i).attributes["control"];
   if (itemControl != null)
   {
      nodeDef[itemName].control = itemControl;
   }
   else
   {
      // TODO: pull this from controls config once we've implemented
      //       a metadata controls element reader
      
      if (itemType == "d:text")
      {
         nodeDef[itemName].control = "/org/alfresco/metadata/controls/textfield.ftl";
      }
      else if (itemType == "d:boolean")
      {
         nodeDef[itemName].control = "/org/alfresco/metadata/controls/boolean.ftl";
      }
   }
}

logger.log("items = " + items);

// create a model for the metadata
var metadataModel = 
{ 
   "itemsToShow": items,
   "data": nodeProps,
   "definition": nodeDef
};

// create model entry for template
model.metadataModel = metadataModel;

*/



