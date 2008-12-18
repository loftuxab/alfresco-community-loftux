// TODOS

// make 'real' call to FormService REST API
// more effecient processing
// get all controls dependent CSS and JS files to pass to head.ftl
// manage 'sets'
// handle error conditions i.e. if a configured field is not valid/present

/*
if (args.nodeRef != null)
{
   var nodeRef = args.nodeRef;
   var formModel = remote.call("/forms/node/" + nodeRef.replace(":/", ""));
}
*/

// fake the JSON response from the FormService and merge the results with the config
var formModel = {};
formModel.data = {};
formModel.data.definition = {};

formModel.data.item = "workspace://SpacesStore/";
formModel.data.submissionUrl = "http://localhost:8081/share/page/form-test";
formModel.data.type = "cm:content";
formModel.data.definition.fields = 
[
{
   "name" : "cm:name",
   "type" : "property",
   "dataType" : "d:text",
   "label" : "Name",
   "description" : "Holds the node's name",
   "mandatory" : "true",
   "enforced" : "true",
   "protected" : "false",
   "repeats" : "false"
},
{
   "name" : "cm:title",
   "type" : "property",
   "dataType" : "d:text",
   "label" : "Title",
   "description" : "Holds the title",
   "mandatory" : "false",
   "enforced" : "false",
   "protected" : "false",
   "repeats" : "false"
},
{
   "name" : "cm:description",
   "type" : "property",
   "dataType" : "d:text",
   "label" : "Description",
   "description" : "Holds the description",
   "mandatory" : "false",
   "enforced" : "false",
   "protected" : "false",
   "repeats" : "false"
}
];
formModel.data.formData = 
{
   "cm:name" : "test.doc",
   "cm:title" : "This is the title",
   "cm:description" : "This is the description"
};

logger.log("formModel = " + formModel);

// create object to hold form ui model
var formUIModel = {};

// determine what mode we are in from the arguments
logger.log("args.mode = " + args.mode);
var mode = args.mode;
if (mode === null)
{
	mode = "edit";
}

logger.log("mode = " + mode);

// setup form ui model basics
formUIModel.mode = mode;
formUIModel.submissionUrl = formModel.data.submissionUrl;
formUIModel.data = formModel.data.formData;

// query for configuration for item
var nodeConfig = config.scoped[formModel.data.type];
logger.log("nodeConfig = " + nodeConfig);

if (nodeConfig != null)
{
   // get the visible fields for the current mode
   var formConfig = nodeConfig.form;
   logger.log("formConfig = " + formConfig);
   
   var visibleFields = null;
   
   switch (mode)
   {
      case "view":
         visibleFields = formConfig.visibleViewFieldNames;
      case "edit":
         visibleFields = formConfig.visibleEditFieldNames;
      case "create":
         visibleFields = formConfig.visibleCreateFieldNames;
      default:
         visibleFields = formConfig.visibleViewFieldNames;
   }
   
   logger.log("visible fields = " + visibleFields);
   
   // get the default controls to use
   var defaultControls = config.global["default-controls"];
   logger.log("default controls = " + defaultControls);
   
   // get the default constraint handlers to use
   var defaultConstraintHandlers = config.global["constraint-handlers"];
   logger.log("constraint handlers = " + defaultConstraintHandlers);
   
   // iterate round each visible field name, retrieve all data and
   // add to the form ui model
   
   // TODO: Make this processs much more effecient i.e. create maps of 
   //       properties and association fields so we can do quick lookups
   var fields = formConfig.fields;
   var items = [];
   
   for (var f = 0; f < visibleFields.size(); f++)
   {
      var fieldName = visibleFields.get(f);
      var fieldConfig = fields[fieldName];

      var fieldDef = null;
      for (var d = 0; d < formModel.data.definition.fields.length; d++)
      {
         var temp = formModel.data.definition.fields[d];
         if (temp.name == fieldName)
         {
            fieldDef = temp;
            break;
         }
      }
      
      // work out which control the field should use, check for explicitly configured
      // one, if that is not present lookup default control based on field and data type
      var control = {};
      control.template = fieldConfig.template;
      if (control.template === null)
      {
         if (fieldDef.type === "property")
         {
            control.template = defaultControls.items[fieldDef.dataType].template;
            
            // TODO: finish processing controls
            var params = defaultControls.items[fieldDef.dataType].controlParams;
            for (var p = 0; p < params.size(); p++)
            {
               
            }
         }
         else
         {
            // TODO: support associations
         }
      }
      
      // TODO: there may be overridden control parameters
      
      
      // create object to represent field and add to items array
      fieldDef.type = "field";
      fieldDef.id = fieldDef.name.replace(":", "_");
      fieldDef.control = control;
      
      // TODO: override field definition with config
      //       i.e. read-only, label, help text, disabled etc.
            
      items.push(fieldDef);
   }
}

formUIModel.items = items;

// pass form ui model to FTL
model.form = formUIModel;

