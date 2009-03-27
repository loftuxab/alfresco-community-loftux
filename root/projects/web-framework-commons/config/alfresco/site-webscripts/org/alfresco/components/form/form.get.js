/* caches */
var propertyFields = null;
var associationFields = null;
var defaultControls = null;
var defaultConstraintHandlers = null;
var formUIConstraints = null;
         
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
      if (logger.isLoggingEnabled())
      {
         logger.log("nodeRef = " + nodeRef);
      }
      
      var json = remote.call("/api/forms/node/" + nodeRef.replace(":/", ""));
      
      if (logger.isLoggingEnabled())
      {
         logger.log("json = " + json);
      }
      
      formModel = eval('(' + json + ')');
      
      if (json.status == 200)
      {
         // setup caches
         setupCaches(formModel);
         
         // determine what mode we are in from the arguments
         var mode = getArgument("mode", "edit");
         
         // determine what enctype to use from the arguments
         var submitType = getArgument("submitType", "multipart");
         var enctype = null;
         switch (submitType)
         {
            case "multipart":
               enctype = "multipart/form-data";
               break;
            case "json":
               enctype = "application/json";
               break;
            case "urlencoded":
               enctype = "application/x-www-form-urlencoded";
               break;
            default:
               enctype = "multipart/form-data";
               break;
         }
         
         // determine what method to use when submitting form
         var mthd = getArgument("method", "POST");
         
         // determine what submisson url to use
         var submissionUrl = getArgument("submissionUrl", formModel.data.submissionUrl);
         submissionUrl = url.context + "/proxy/alfresco" + submissionUrl;
         
         // determine whether to show caption at top of form
         var showCaption = getArgument("showCaption", "false");
         
         // determine whether to show cancel button
         var showCancelButton = getArgument("showCancelButton", "false");
         
         // determine whether to show reset button
         var showResetButton = getArgument("showResetButton", "false");
         
         // create and setup form ui model basics
         formUIModel = {};
         formUIModel.mode = mode;
         formUIModel.method = mthd;
         formUIModel.enctype = enctype;
         formUIModel.submissionUrl = submissionUrl;
         formUIModel.showCaption = (showCaption === "true") ? true : false;
         formUIModel.showCancelButton = (showCancelButton === "true") ? true : false;
         formUIModel.showResetButton = (showResetButton === "true") ? true : false;
         formUIModel.data = formModel.data.formData;
         
         // query for configuration for item
         var nodeConfig = config.scoped[nodeRef];
         
         if (nodeConfig != null)
         {
            // get the visible fields for the current mode
            var formConfig = nodeConfig.form;
            
            if (formConfig != null)
            {
               var visibleFields = null;
               
               // TODO: deal with hidden vs. show mode and no config
               
               switch (mode)
               {
                  case "view":
                     visibleFields = formConfig.visibleViewFieldNames;
                     break;
                  case "edit":
                     visibleFields = formConfig.visibleEditFieldNames;
                     break;
                  case "create":
                     visibleFields = formConfig.visibleCreateFieldNames;
                     break;
                  default:
                     visibleFields = formConfig.visibleViewFieldNames;
                     break;
               }
               
               if (logger.isLoggingEnabled())
               {
                  logger.log("Visible fields for " + formUIModel.mode + " mode = " + visibleFields);
               }
               
               // iterate round each visible field name, retrieve all data and
               // add to the form ui model
               var configuredFields = formConfig.fields;
               var formUIItems = [];
               
               if (visibleFields != null)
               {
                  for (var f = 0; f < visibleFields.size(); f++)
                  {
                     var fieldName = visibleFields.get(f);
                     var fieldConfig = configuredFields[fieldName];
                     
                     // setup the field
                     var fieldDef = setupField(formModel, fieldName, fieldConfig);
                     
                     // if a field was created add to the list to be displayed
                     if (fieldDef !== null)
                     {
                        formUIItems.push(fieldDef);
                        
                        if (logger.isLoggingEnabled())
                        {
                           logger.log("Added field definition for \"" + fieldName + "\" " + jsonUtils.toJSONString(fieldDef));
                        }
                     }
                  }
               }
               else
               {
                  model.error = "No fields to render for node type \"" + formModel.data.type + "\".";
               }
            }
            else
            {
               // TODO: This should just show all properties instead
               model.error = "No configuration found for node type \"" + formModel.data.type + "\".";
            }
         }
         
         // TODO: deal with 'sets', the fields must be within their appropriate set
         //       and structured with the correct hierarchy, should this be done in
         //       here or is it up to the config service to determine the hierarchy?
         
         // TODO: see if there is an overidden submissionUrl in the form config
         
         formUIModel.items = formUIItems;
         formUIModel.constraints = formUIConstraints;
      }
      else
      {
         model.error = formModel.message;
      }
   }
   
   // log the model
   //dumpFormUIModel(formUIModel);
   
   // pass form ui model to FTL
   model.form = formUIModel;
}

/**
 * Retrieves the value of the given named argument, looks in the 
 * URL arguments and the component binding properties
 *
 * @method getArgument
 * @param argName The name of the argument to locate
 * @param defValue The default value to use if the argument could not be found
 * @return The value or null if not found
 */
function getArgument(argName, defValue)
{
   var result = null;
   
   if (typeof defValue !== "undefined")
   {
      result = defValue;
   }
   
   var argValue = args[argName];
   if (argValue !== null)
   {
      result = argValue;
   }
   else
   {
      argValue = context.properties[argName];
      if (argValue !== null)
      {
   	   result = argValue;
      }
   }
   
   return result;
}

/**
 * Sets up the caches used by other functions in this script
 *
 * @method setupCaches
 * @param formModel The form model returned from the server
 */
function setupCaches(formModel)
{
   propertyFields = {};
   associationFields = {};
   formUIConstraints = [];
   
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
 *                    or null if there isn't any configuration 
 * @return Object representing the field 
 */
function setupField(formModel, fieldName, fieldConfig)
{
   var fieldDef = null;
   
   // look in both caches for the field
   var propFieldDef = propertyFields[fieldName];
   var assocFieldDef = associationFields[fieldName];
   
   // check that the field is not ambiguous i.e. if there is an association and a
   // property in the model with the same name the appropriate prefix should be 
   // used to uniquely identify the field
   if (typeof propFieldDef !== "undefined" && typeof assocFieldDef !== "undefined")
   {
      if (logger.isWarnLoggingEnabled())
      {
         logger.warn("\"" + fieldName + "\" is ambiguous, a property and an association exists with this name, prefix with either \"prop:\" or \"assoc:\" to uniquely identify the field");
      }
      
      fieldDef = createTransientField(fieldName, { template: "controls/ambiguous.ftl" });
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
         fieldDef.configName = fieldName;
         // force the conversion to a JavaScript string object so replace() can be used below
         var name = "" + fieldName;
         if (fieldDef.type === "association") 
         {
            // check that the association does not already have the prefix
            if (name.indexOf(ASSOC_PREFIX) !== 0)
            {
               name = ASSOC_PREFIX + fieldName;
            }
         }
         else
         {
            // check that the property does not already have the prefix
            if (name.indexOf(PROP_PREFIX) !== 0)
            {
               name = PROP_PREFIX + fieldName;
            }
         }         
         fieldDef.name = name.replace(/:/g, "_");
         fieldDef.id = fieldDef.name;
         
         // setup the state of the field i.e. if it's active or disabled
         setupFieldState(fieldDef, fieldConfig);
   
         // setup the control for the field
         setupFieldControl(fieldDef, fieldConfig);
   
         // setup text for the field i.e. the label, description & help
         setupFieldText(fieldDef, fieldConfig);
         
         // setup constraints for the field
         setupFieldConstraints(fieldDef, fieldConfig);
         
         // setup the value for the field
         setupFieldValue(formModel, fieldDef, fieldConfig);
      }
      else
      {
         // the field does not have a definition but may be a 'transient' field
         // if there is at least a control template create a transient field
         if (fieldConfig !== null && fieldConfig.template !== null)
         {
            fieldDef = createTransientField(fieldName, fieldConfig);
         }
         else if (logger.isWarnLoggingEnabled())
         {
            logger.warn("Ignoring field \"" + fieldName + "\" as a field definition or configuration could not be located");
         }
      }
   }
   
   return fieldDef;
}

/**
 * Sets up the state of the field i.e. whether it's active or disabled.
 *
 * @method setupFieldState
 * @param fieldDef Object representing the field definition 
 * @param fieldConfig Object representing the UI configuration
 *                    or null if there isn't any configuration
 */
function setupFieldState(fieldDef, fieldConfig)
{
   // configure read-only state (and remove protectedField property)
   var disabled = fieldDef.protectedField;
   if (!fieldDef.protectedField && fieldConfig !== null && fieldConfig.readOnly)
   {
      disabled = true;
   }
   
   fieldDef.disabled = disabled;
   delete fieldDef.protectedField;
}

/**
 * Sets up the control for the field.
 *
 * @method setupFieldControl
 * @param fieldDef Object representing the field definition 
 * @param fieldConfig Object representing the UI configuration
 *                    or null if there isn't any configuration
 */
function setupFieldControl(fieldDef, fieldConfig)
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
   if (fieldConfig !== null && fieldConfig.template !== null)
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
         else if (logger.isWarnLoggingEnabled())
         {
            logger.warn("No default control found for data type \"" + fieldDef.dataType + "\" whilst processing field \"" + fieldDef.configName + "\"");
         }
      }
      else
      {
         if (defaultControlConfig !== null)
         {
            control.template = defaultControlConfig.template;
         }
         else if (logger.isWarnLoggingEnabled())
         {
            logger.warn("No default control found for associations" + "\" whilst processing field \"" + fieldDef.configName + "\"");
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
   if (fieldConfig !== null)
   {
      var paramsConfig = fieldConfig.controlParams;
      for (var p = 0; p < paramsConfig.size(); p++)
      {
         control.params[paramsConfig.get(p).name] = paramsConfig.get(p).value;
      }
   }
   
   fieldDef.control = control;
}

/**
 * Sets up the textual aspects of the field i.e. the label, 
 * description and help text
 *
 * @method setupFieldText
 * @param fieldDef Object representing the field definition 
 * @param fieldConfig Object representing the UI configuration
 *                    or null if there isn't any configuration
 */
function setupFieldText(fieldDef, fieldConfig)
{
   if (fieldConfig !== null)
   {
      // process label
      var configLabel = null;
      
      if (fieldConfig.labelId !== null)
      {
         configLabel = msg.get(fieldConfig.labelId);
      }
      else if (fieldConfig.label !== null)
      {
         configLabel = fieldConfig.label;
      }
      
      if (configLabel !== null)
      {
         fieldDef.label = configLabel;
      }

      // process description
      var configDesc = null;
      
      if (fieldConfig.descriptionId !== null)
      {
         configDesc = msg.get(fieldConfig.descriptionId);
      }
      else if (fieldConfig.description !== null)
      {
         configDesc = fieldConfig.description;
      }
      
      if (configDesc !== null)
      {
         fieldDef.description = configDesc;
      }
      
      // process help
      var configHelp = null;
      
      if (fieldConfig.helpTextId !== null)
      {
         configHelp = msg.get(fieldConfig.helpTextId);
      }
      else if (fieldConfig.helpText !== null)
      {
         configHelp = fieldConfig.helpText;
      }
      
      if (configHelp !== null)
      {
         fieldDef.help = configHelp;
      }
   }
}

/**
 * Sets up the constraints for the field, if it has any
 *
 * @method setupFieldConstraints
 * @param fieldDef Object representing the field definition 
 * @param fieldConfig Object representing the UI configuration
 *                    or null if there isn't any configuration
 */
function setupFieldConstraints(fieldDef, fieldConfig)
{
   // setup mandatory constraint if field is marked as such
   if (fieldDef.mandatory && fieldDef.disabled == false)
   {
      var constraint = createFieldConstraint("MANDATORY", {}, fieldDef, fieldConfig);
      
      if (constraint !== null)
      {
         // add the constraint to the global list
         formUIConstraints.push(constraint);
      }
   }
   
   // setup number constraint if field is a number
   if (fieldDef.dataType === "d:int" || fieldDef.dataType === "d:long" || 
       fieldDef.dataType === "d:double" || fieldDef.dataType === "d:float" )
   {
      var constraint = createFieldConstraint("NUMBER", {}, fieldDef, fieldConfig);
      
      if (constraint !== null)
      {
         // add the constraint to the global list
         formUIConstraints.push(constraint);
      }
   }
   
   // look for model defined constraints on the field definition
   if (typeof fieldDef.constraints !== "undefined")
   {
      for (var c = 0; c < fieldDef.constraints.length; c++)
      {
         var obj = fieldDef.constraints[c];
         var constraint = createFieldConstraint(obj.type, obj.params, fieldDef, fieldConfig);
      
         if (constraint !== null)
         {
            // add the constraint to the global list
            formUIConstraints.push(constraint);
         }
      }
   }

   // remove the constraints property from the fieldDef object
   delete fieldDef.constraints;
}

/**
 * Creates an object representing the constraint with the given
 * id for the given field definition and configuration
 *
 * @method createFieldConstraint
 * @param constraintId The contstraint identifier, for example "REGEX"
 * @param constraintParams Object representing parameters to pass to the client side handler
 * @param fieldDef Object representing the field definition 
 * @param fieldConfig Object representing the UI configuration
 *                    or null if there isn't any configuration
 * @return An object representing the constraint or null if it could
 *         not be constructed
 */
function createFieldConstraint(constraintId, constraintParams, fieldDef, fieldConfig)
{
   var constraint = null;
   
   var defaultConstraintConfig = defaultConstraintHandlers.items[constraintId];
   if (defaultConstraintConfig !== null)
   {
      constraint = {};
      constraint.fieldId = fieldDef.id;
      constraint.validationHandler = defaultConstraintConfig.validationHandler;
      constraint.params = jsonUtils.toJSONString(constraintParams);
      if (defaultConstraintConfig.event !== null && defaultConstraintConfig.event !== "")
      {
         constraint.event = defaultConstraintConfig.event;
      }
      else
      {
         constraint.event = "blur";
      }
      
      // look for an overridden message in the field's constraint config, 
      // if none found look in the default constraint config
      
      var constraintMsg = null;
      if (fieldConfig !== null && fieldConfig.constraintMessageMap[constraintId] !== null)
      {
         var fieldConstraintConfig = fieldConfig.constraintMessageMap[constraintId];
         if (fieldConstraintConfig.messageId !== null)
         {
            constraintMsg = msg.get(fieldConstraintConfig.messageId);
         }
         else if (fieldConstraintConfig.message !== null)
         {
            constraintMsg = fieldConstraintConfig.message;
         }
      }
      else if (typeof defaultConstraintConfig.messageId !== "undefined" && 
               defaultConstraintConfig.messageId !== null)
      {
         constraintMsg = msg.get(defaultConstraintConfig.messageId);
      }
      else if (typeof defaultConstraintConfig.message !== "undefined" && 
               defaultConstraintConfig.message !== null)
      {
         constraintMsg = defaultConstraintConfig.message;
      }
      
      // add the message if there is one
      if (constraintMsg != null)
      {
         constraint.message = constraintMsg;
      }
      
      if (logger.isLoggingEnabled())
      {
         logger.log("Built constraint: " + jsonUtils.toJSONString(constraint));
      }
      
      if (constraintId === "LIST")
      {
         // if the constraint is the list of values constraint force the control
         // template to be select.ftl and setup the options
         fieldDef.control.template = "controls/select.ftl";
         
         // setup the options string and set as control params
         fieldDef.control.params.options = constraintParams.allowedValues;
      }
   }
   else if (logger.isWarnLoggingEnabled())
   {
      logger.warn("No default constraint configuration found for \"" + constraintId + "\" constraint whilst processing field \"" + fieldDef.configName + "\"");
   }
   
   return constraint;
}

/**
 * Sets up the value of the field.
 *
 * @method setupFieldValue
 * @param formModel The form model returned from the server
 * @param fieldDef Object representing the field definition 
 * @param fieldConfig Object representing the UI configuration
 *                    or null if there isn't any configuration
 */
function setupFieldValue(formModel, fieldDef, fieldConfig)
{
   fieldDef.value = "";
   
   if (typeof formModel.data.formData[fieldDef.name] !== "undefined")
   {
      fieldDef.value = formModel.data.formData[fieldDef.name];
   }
}

/**
 * Creates a transient field that represents a field to display
 * that does not have a corresponding field definition.
 *
 * @method createTransientField
 * @param fieldName The name of the transient field as defined in the configuration
 * @param fieldConfig Object representing the UI configuration of the field, 
          MUST not be null and MUST define at least a control template.
 * @return Object representing the transient field or null if it could not be created
 */
function createTransientField(fieldName, fieldConfig)
{
   // we can't continue without at least a control template
   if (fieldConfig === null || fieldConfig.template === null)
   {
      return null;
   }
   
   var fieldDef = {};
   
   // apply defaults for transient field
   fieldDef.kind = "field";
   fieldDef.configName = fieldName;
   // force the name to convert to a JavaScript string so replace method can be used
   var name = "" + fieldName;
   fieldDef.name = name.replace(/:/g, "_");
   fieldDef.id = fieldDef.name;
   fieldDef.label = fieldName;
   fieldDef.value = "";
   fieldDef.transitory = true;
   fieldDef.mandatory = false;
   fieldDef.disabled = false;
   
   // setup control
   fieldDef.control = {};
   fieldDef.control.template = fieldConfig.template;
   
   var params = {};
   if (typeof fieldConfig.controlParams !== "undefined")
   {
      var paramsConfig = fieldConfig.controlParams;
      for (var p = 0; p < paramsConfig.size(); p++)
      {
         params[paramsConfig.get(p).name] = paramsConfig.get(p).value;
      }
   }
   fieldDef.control.params = params;
   
   // apply any configured text
   setupFieldText(fieldDef, fieldConfig);
   
   return fieldDef;
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
         debug = jsonUtils.toJSONString(model);
      }
      logger.log("formUIModel = " + debug);
   }
}

main();
