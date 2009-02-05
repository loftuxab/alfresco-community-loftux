/* caches */
var propertyFields = null;
var associationFields = null;
var defaultControls = null;
var defaultConstraintHandlers = null;
         
/* constants */
const PROP_PREFIX = "prop:"
const ASSOC_PREFIX = "assoc:";

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
   var formModel = null;
   var formUIModel = null;
   
   if (context.properties.nodeRef != null && context.properties.nodeRef != "")
   {
      var nodeRef = context.properties.nodeRef;
      logger.log("nodeRef = " + nodeRef);
      
      var json = remote.call("/api/forms/node/" + nodeRef.replace(":/", ""));
      
      // TODO: test for response status and potential errors
      
      if (logger.isLoggingEnabled())
      {
         logger.log("json = " + json);
      }
      
      formModel = eval('(' + json + ')');
      
      // setup caches
      setupCaches(formModel);
      
      // determine what mode we are in from the arguments
      var mode = context.properties.mode;
      if (mode === null)
      {
      	mode = "edit";
      }
      
      // create and setup form ui model basics
      formUIModel = {};
      formUIModel.mode = mode;
      formUIModel.submissionUrl = formModel.data.submissionUrl;
      
      // query for configuration for item
      var nodeConfig = config.scoped[formModel.data.type];
      
      if (nodeConfig != null)
      {
         // get the visible fields for the current mode
         var formConfig = nodeConfig.form;
         
         var visibleFields = null;
         
         // TODO: deal with hidden vs. show mode and no config
         
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
         
         logger.log("visible fields for " + formUIModel.mode + " mode = " + visibleFields);
         
         // iterate round each visible field name, retrieve all data and
         // add to the form ui model
         var configuredFields = formConfig.fields;
         var formUIItems = [];
         
         for (var f = 0; f < visibleFields.size(); f++)
         {
            var fieldName = visibleFields.get(f);            
            var fieldConfig = configuredFields[fieldName];
            
            if (fieldConfig === null)
            {
               // if the field does not appear in the appearance section
               // there won't be any config so create empty object
               fieldConfig = {};
            }
            
            // setup the field
            var fieldDef = setupField(formModel, fieldName, fieldConfig);
            
            // if a field was created add to the list to be displayed
            if (fieldDef !== null)
            {
               formUIItems.push(fieldDef);
               
               logger.log(fieldName + " = " + jsonUtils.toJSONString(fieldDef));
            }
         }
      }
      
      // TODO: deal with 'sets', the fields must be within their appropriate set
      //       and structured with the correct hierarchy, should this be done in
      //       here or is it up to the config service to determine the hierarchy?
      
      formUIModel.items = formUIItems;
   }
   
   // log the model
   //dumpFormUIModel(formUIModel);
   
   // pass form ui model to FTL
   model.form = formUIModel;
}

/**
 * Sets up the caches used by other functions in this script
 *
 * @method setupCaches
 */
function setupCaches(formModel)
{
   propertyFields = {};
   associationFields = {};
   
   // iterate over fields array and cache the properties and associations separately
   for (var d = 0; d < formModel.data.definition.fields.length; d++)
   {
      var field = formModel.data.definition.fields[d];
      if (field.type === "property")
      {
         propertyFields[field.name] = field;
      }
      else if (field.type === "association")
      {
         associationFields[field.name] = field;
      }
   }
   
   // get the default controls configuration
   defaultControls = config.global["default-controls"];
   
   // get the default constraint handlers configuration
   defaultConstraintHandlers = config.global["constraint-handlers"];
}

/**
 * Sets up a field, the result of which is a combination of the
 * field defintion and field UI configuration.
 *
 * @method setupField
 * @param formModel The form model returned from the server
 * @param fieldName The name of the field to setup
 * @param fieldConfig Object representing the UI configuration
 * @return Object representing the field 
 */
function setupField(formModel, fieldName, fieldConfig)
{
   logger.log("setting up field " + fieldName);
   
   var fieldDef = null;
   
   // look in both caches for the field
   var propFieldDef = propertyFields[fieldName];
   var assocFieldDef = associationFields[fieldName];
   
   // check that the field is not ambiguous i.e. if there is an association and a
   // property in the model with the same name the appropriate prefix should be 
   // used to uniquely identify the field
   if (typeof propFieldDef !== "undefined" && typeof assocFieldDef !== "undefined")
   {
      logger.log("WARN: \"" + fieldName + "\" is ambiguous, a property and an association exists with this name, prefix with either \"prop:\" or \"assoc:\" to uniquely identify the field");
      fieldDef = {};
      fieldDef.name = fieldName;
      fieldDef.kind = "field";
      fieldDef.id = fieldDef.name.replace(":", "_");
      fieldDef.control = {};
      fieldDef.control.template = "controls/ambiguous.ftl";
   }
   else
   {
      if (typeof propFieldDef === "undefined" && typeof assocFieldDef === "undefined")
      {
         // if a field definition has not been found yet check for prop: and assoc: prefixes
         if (fieldName.indexOf(PROP_PREFIX) != -1)
         {
            propFieldDef = propertyFields[fieldName.substring(PROP_PREFIX.length)];
         }
         else if (fieldName.indexOf(ASSOC_PREFIX) != -1)
         {
            assocFieldDef = associationFields[fieldName.substring(ASSOC_PREFIX.length)];
         }
      }

      // determine if field was a property or association
      if (typeof propFieldDef !== "undefined")
      {
         fieldDef = propFieldDef;
      }
      else if (typeof assocFieldDef !== "undefined")
      {
         fieldDef = assocFieldDef;
      }
      
      if (fieldDef !== null)
      {
         // setup the basic properties
         fieldDef.kind = "field";
         fieldDef.name = (fieldDef.type === "association") ? ASSOC_PREFIX + fieldName : PROP_PREFIX + fieldName;
         fieldDef.id = fieldDef.name.replace(/:/g, "_");
         
         // setup appearance of field i.e. the control, label etc.
         setupAppearance(fieldDef, fieldConfig);
         
         // set the value for the field
         fieldDef.value = "";
         if (typeof formModel.data.formData[fieldDef.name] !== "undefined")
         {
            fieldDef.value = formModel.data.formData[fieldDef.name];
         }
      }
      else
      {
         logger.log("Ignoring field \"" + fieldName + "\" as a field definition could not be located");
      }
   }
   
   return fieldDef;
}

/**
 * Sets up the appearance of the field.
 *
 * @method setupAppearance
 * @param fieldDef Object representing the field definition 
 * @param fieldConfig Object representing the UI configuration
 */
function setupAppearance(fieldDef, fieldConfig)
{
   // setup the control object
   setupControl(fieldDef, fieldConfig);
   
   // TODO: update this function when the FormField class gets explicit accessors
   //       for now exit if there are no attributes for the field
   if (typeof fieldConfig.attributes === "undefined" || fieldConfig.attributes === null)
   {
      return;
   }
   
   // override the label if necessary
   var configLabel = null;
   if (fieldConfig.attributes["label-id"] !== null)
   {
      configLabel = msg.get(fieldConfig.attributes["label-id"]);
   }
   else if (fieldConfig.attributes["label"] !== null)
   {
      configLabel = fieldConfig.attributes["label"];
   }
   if (configLabel !== null)
   {
      fieldDef.label = configLabel;
   }
   
   // override the description if necessary
   var configTitle = null;
   if (fieldConfig.attributes["title-id"] !== null)
   {
      configTitle = msg.get(fieldConfig.attributes["title-id"]);
   }
   else if (fieldConfig.attributes["title"] !== null)
   {
      configTitle = fieldConfig.attributes["title"];
   }
   if (configTitle !== null)
   {
      fieldDef.description = configTitle;
   }
   
   // find help text, if any
   var configHelp = null;
   if (fieldConfig.attributes["help-text-id"] !== null)
   {
      configHelp = msg.get(fieldConfig.attributes["help-text-id"]);
   }
   else if (fieldConfig.attributes["help-text"] !== null)
   {
      configHelp = fieldConfig.attributes["help-text"];
   }
   if (configHelp !== null)
   {
      fieldDef.help = configHelp;
   }
   
   // configure read-only state
   if (!fieldDef.protectedField && fieldConfig.attributes["read-only"] !== null)
   {
      var readOnly = fieldConfig.attributes["read-only"];
      if (readOnly === "true")
      {
         fieldDef.protectedField = true;
      }
   }   
}

/**
 * Sets up the control for the field.
 *
 * @method setupControl
 * @param fieldDef Object representing the field definition 
 * @param fieldConfig Object representing the UI configuration
 */
function setupControl(fieldDef, fieldConfig)
{
   // construct the control object for the field
   var control = {};
   control.params = {};
   
   var defaultControlConfig = null;
   var isPropertyField = (fieldDef.type !== "association");
   
   if (isPropertyField)
   {
      // get the default control for the property data type
      defaultControlConfig = defaultControls.items[fieldDef.dataType];
   }
   else
   {
      // get the default control for associations
      defaultControlConfig = defaultControls.items["association"];
   }
   
   // see if the fieldConfig already has a template defined, if not 
   // retrive the default template for the field's data type
   if (typeof fieldConfig.template !== "undefined" && fieldConfig.template !== null)
   {
      control.template = fieldConfig.template;
   }
   else
   {
      if (isPropertyField)
      {
         if (defaultControlConfig !== null)
         {
            control.template = defaultControlConfig.template;
         }
         else
         {
            logger.log("WARN: no default control found for data type \"" + fieldDef.dataType + "\"");
         }
      }
      else
      {
         if (defaultControlConfig !== null)
         {
            control.template = defaultControlConfig.template;
         }
         else
         {
            logger.log("WARN: no default control found for associations");
         }
      }
   }
   
   // get control parameters for the default control (if there is one)
   if (defaultControlConfig !== null)
   {
      var paramsConfig = defaultControlConfig.controlParams;
      for (var p = 0; p < paramsConfig.size(); p++)
      {
         control.params[paramsConfig.get(p).name] = paramsConfig.get(p).value;
      }
   }
   
   // get overridden control parameters (if there are any)
   if (typeof fieldConfig.controlParams !== "undefined")
   {
      var fieldControlParams = fieldConfig.controlParams;
      for (p in fieldControlParams)
      {
         control.params[p] = fieldControlParams[p];
      }
   }
   
   fieldDef.control = control;
}

/**
 * Dumps the form UI model, but only if logging is active
 *
 * @method dumpFormUIModel
 * @param model Object representing the form UI model
 */
function dumpFormUIModel(model)
{
   // dump the form UI model when debug is active
   if (logger.isLoggingEnabled())
   {
      var debug = "null";
      if (model != null)
      {
         debug = jsonUtils.toJSONString(model)
      }
      logger.log("formUIModel = " + debug);
   }
}

main();
