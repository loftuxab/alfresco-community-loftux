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
   var formUIModel = null;
   
   var itemKind = getArgument("itemKind");
   var itemId = getArgument("itemId");
   
   if (itemKind != null && itemKind.length > 0 && itemId != null && itemId.length > 0)
   {
      // determine what mode we are in from the arguments
      var mode = getArgument("mode", "edit");
      
      // determine if a form with a specific form is required
      var formId = getArgument("formId");

      if (logger.isLoggingEnabled())
      {
         logger.log("Showing form (id=" + formId + ") for item: [" + itemKind + "]" + itemId);
      }
      
      // get the config for the form
      var formConfig = getFormConfig(itemId, formId);
      
      // get the configured visible fields
      var visibleFields = getVisibleFields(mode, formConfig);
      
      // build the JSON object to send to the server
      var postBody = createPostBody(itemKind, itemId, visibleFields, formConfig);
         
      // make remote call to service
      var connector = remote.connect("alfresco");
      var json = connector.post("/api/formdefinitions", 
            jsonUtils.toJSONString(postBody), "application/json");
      
      if (logger.isLoggingEnabled())
      {
         logger.log("json = " + json);
      }
      
      var formModel = eval('(' + json + ')');
      
      // if we got a successful response attempt to render the form
      if (json.status == 200)
      {
         // setup caches and variables
         setupCaches(formModel);
         
         // setup the initial form ui model
         formUIModel = setupFormUIModel(mode, formModel, formConfig);
         
         // setup and add items to form ui model
         formUIModel.items = setupFormUIItems(mode, formModel, formConfig, visibleFields);
         
         // constraints were built during form items construction, add 
         // them to the form ui model
         formUIModel.constraints = formUIConstraints;
         
         // add the item kind and id arguments
         formUIModel.arguments =
         {
            itemKind: itemKind,
            itemId: itemId
         };
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
   
   var argValue = null;
   try
   {
      argValue = instance.properties[argName];
   }
   catch (e)
   {
      argValue = null;
   }
   if (argValue !== null)
   {
      if (argValue.length > 0)
      {
         // check for parameterised values i.e. {xyz}
         // if found leave result resolution to 'args' map
         // as the value will have been resolved
         if (argValue.charAt(0) !== "{" || 
             argValue.charAt(argValue.length-1) !== "}")
         {
            result = argValue;
         }
      }
      else
      {
         result = "";
      }
   }
   
   // if result is null try the 'context.properties' map
   if (result === null)
   {
      argValue = context.properties[argName];
      if (argValue !== null)
      {
         if (argValue.length > 0)
         {
            // check for parameterised values i.e. {xyz}
            // if found leave result resolution to 'args' map
            // as the value will have been resolved
            if (argValue.charAt(0) !== "{" || 
                argValue.charAt(argValue.length-1) !== "}")
            {
               result = argValue;
            }
         }
         else
         {
            result = "";
         }
      }
   }
   
   // if result is still null try the 'args' map
   if (result === null)
   {
      argValue = args[argName];
      if (argValue !== null)
      {
   	   result = argValue;
      }
   }
   
   // if we still don't have a result and a default has been
   // defined, return that instead
   if (result === null && typeof defValue !== "undefined")
   {
      result = defValue;
   }
   
   if (logger.isLoggingEnabled())
   {
      logger.log("Returning \"" + result + "\" from getArgument for \"" + argName + "\"");
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
   defaultControls = config.global.forms.defaultControls;
   
   // get the default constraint handlers configuration
   defaultConstraintHandlers = config.global.forms.constraintHandlers;
}


/**
 * Finds the configuration for the given item id, if
 * there isn't any configuration for the item null is
 * returned.
 *
 * @method getFormConfig
 * @param itemId The id of the item to retrieve for config for
 * @param formId The id of the specific form to lookup or null
 *               to get the default form
 * @return Object representing the configuration or null
 */
function getFormConfig(itemId, formId)
{
   var formConfig = null;
   
   // query for configuration for item
   var nodeConfig = config.scoped[itemId];
   
   if (nodeConfig !== null)
   {
      // get the forms configuration
      var formsConfig = nodeConfig.forms;

      if (formsConfig !== null)
      {
         if (formId !== null && formId.length > 0)
         {
            // look up the specific form
            formConfig = formsConfig.getForm(formId);
         }
         else
         {
            // look up the default form
            formConfig = formsConfig.defaultForm;
         }
      }
   }
   
   return formConfig;
}

/**
 * Returns the list of fields configured to be visible for the 
 * given mode. If this method returns null or an empty list the
 * component should attempt to display ALL known data for the item, 
 * unless there are fields configured to be hidden.
 *
 * @method getVisibleFields
 * @param mode The mode the form is rendering, 'view', 'edit' or 'create'
 * @param formConfig The form configuration, maybe null
 * @return Array of field names or null
 */
function getVisibleFields(mode, formConfig)
{
   var visibleFields = null;
   
   if (formConfig != null)
   {
      // get visible fields for the current mode
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
   }
   
   if (logger.isLoggingEnabled())
   {
      logger.log("Fields configured to be visible for " + mode + " mode = " + visibleFields);
   }
         
   return visibleFields;
}


/**
 * Creates an Object to represent the body of the POST request
 * to send to the form service.
 *
 * @method createPostBody
 * @param itemKind The kind of item
 * @param itemId The id of the item
 * @param visibleFields List of fields to get data for
 * @param formConfig The form configuration object
 * @return Object representing the POST body
 */
function createPostBody(itemKind, itemId, visibleFields, formConfig)
{
   var postBody = {};
   
   postBody.itemKind = itemKind;
   postBody.itemId = itemId.replace(":/", "");
   
   if (visibleFields !== null)
   {
      // TODO: find a way to return/make a native JS array, for now
      //       convert the Java List to a JS array checking force as we go
      var postBodyFields = [];
      var postBodyForcedFields = [];
      var fieldId = null;
      for (var f = 0; f < visibleFields.length; f++)
      {
         fieldId = visibleFields[f]
         postBodyFields.push(fieldId);
         if (formConfig.isFieldForced(fieldId))
         {
            postBodyForcedFields.push(fieldId);
         }
      }
      
      postBody.fields = postBodyFields;
      if (postBodyForcedFields.length > 0)
      {
         postBody.force = postBodyForcedFields;
      }
   }
   
   if (logger.isLoggingEnabled())
   {
      logger.log("postBody = " + jsonUtils.toJSONString(postBody));
   }
      
   return postBody;
}

/**
 * Sets up the basics of the form UI model used to generate the UI
 *
 * @method setupFormUIModel
 * @param mode The mode of the form
 * @param formModel The model returned from the server
 * @param formConfig The form configuration
 * @return Object representing the form UI model
 */
function setupFormUIModel(mode, formModel, formConfig)
{
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
   var submissionUrl = null;
   if (formConfig !== null && formConfig.submissionURL !== null)
   {
      submissionUrl = formConfig.submissionURL;
   }
   else
   {   
      submissionUrl = getArgument("submissionUrl", formModel.data.submissionUrl);
   }
   submissionUrl = url.context + "/proxy/alfresco" + submissionUrl;
   
   // determine whether to show caption at top of form
   var showCaption = getArgument("showCaption", "false");
   
   // determine whether to show cancel button
   var showCancelButton = getArgument("showCancelButton", "false");
   
   // determine whether to show reset button
   var showResetButton = getArgument("showResetButton", "false");
   
   // create and setup form ui model basics
   var formUIModel =
   {
      mode: mode,
      method: mthd,
      enctype: enctype,
      submissionUrl: submissionUrl,
      showCaption: (showCaption === "true") ? true : false,
      showCancelButton: (showCancelButton === "true") ? true : false,
      showResetButton: (showResetButton === "true") ? true : false,
      data: formModel.data.formData
   };
   
   return formUIModel;
}

/**
 * Sets up the items property of the form ui model, this consists
 * of the set and field structure that subsequently needs to be 
 * rendered.
 *
 * @method setupFormUIItems
 * @param mode The mode of the form
 * @param formModel The model returned from the server
 * @param formConfig The form configuration
 * @visibleFields List of fields configured to be visible
 * @return Object representing the form UI model
 */
function setupFormUIItems(mode, formModel, formConfig, visibleFields)
{
   var formUIItems = [];
   
   // setup the set and field structure
   if (visibleFields !== null && visibleFields.length > 0)
   {
      // if we have visible fields we can presume there is
      // config present!
      
      // get the root sets
      var rootSets = formConfig.rootSets;      
      for (var s = 0; s < rootSets.length; s++)
      {
         var set = setupSet(mode, rootSets[s], formModel, formConfig);
         // if the set got created (as it contained fields or other sets) add to items list
         if (set !== null)
         {
            formUIItems.push(set);
         }
      }
   }
   else
   {
      // as we have no visible fields and potentially no config the
      // default behaviour is to show all known fields for the item
      
      // TODO: if there is no config at all just show all fields
      
      // TODO: iterate around all fields returned from the server
      //       and determine what set they belong to
      
      // TODO: get root sets from config and build set structure using
      //       config and list built above
      
      // for now return an error message
      model.error = "Not enough configuration found for node type \"" + formModel.data.type + "\", at least one &lt;show&gt; element must be present.";
   }
   
   return formUIItems;
}

/**
 * Sets up the item to represent the given set definition.
 * The item returned represents the set and field structure
 * defined in the form config.
 *
 * @method createSetItem
 * @param mode The mode of the form
 * @param setConfig The set configuration
 * @param formModel The model returned from the server 
 * @param formConfig The form configuration
 */
function setupSet(mode, setConfig, formModel, formConfig)
{
   var set = null;
   
   // get the fields to display in this set
   var fieldsForSet = null;
   
   switch (mode)
   {
      case "view":
         fieldsForSet = formConfig.getVisibleViewFieldNamesForSet(setConfig.setId);
         break;
      case "edit":
         fieldsForSet = formConfig.getVisibleEditFieldNamesForSet(setConfig.setId);
         break;
      case "create":
         fieldsForSet = formConfig.getVisibleCreateFieldNamesForSet(setConfig.setId);
         break;
      default:
         fieldsForSet = formConfig.getVisibleViewFieldNamesForSet(setConfig.setId);
         break;
   }
   
   // if there is something to show in the set create the set object
   if ((fieldsForSet !== null && fieldsForSet.length > 0) || setConfig.children.length > 0)
   {
      // setup the basic set object
      set = {};
      set.kind = "set";
      set.id = setConfig.setId;
      set.appearance = setConfig.appearance;
      set.children = [];
      
      // work out label to use
      var label = null;
            
      if (setConfig.labelId !== null)
      {
         label = msg.get(setConfig.labelId);
      }
      else if (setConfig.label !== null)
      {
         label = setConfig.label;
      }
      else
      {
         // if there is no label specified in the config,
         // use the label from the properties file otherwise
         // use the set id
         if (setConfig.setId + "" === "")
         {
            label = msg.get("form.default.set.label");
         }
         else
         {
            label = setConfig.setId;
         }
      }
      
      set.label = label;
      
      // add all the fields to the set
      for (var f = 0; f < fieldsForSet.length; f++)
      {
         var fieldName = fieldsForSet[f];
         var fieldConfig = formConfig.fields[fieldName];
         
         // setup the field
         var field = setupField(formModel, fieldName, fieldConfig);
         
         // if a field was created add to the set's list of children
         if (field !== null)
         {
            set.children.push(field);
            
            if (logger.isLoggingEnabled())
            {
               logger.log("Added field \"" + fieldName + "\" to set \"" + set.id + "\": " + 
                     jsonUtils.toJSONString(field));
            }
         }
      }
      
      // recursively setup child sets
      for (var c = 0; c < setConfig.children.length; c++)
      {
         var childSet = setupSet(mode, setConfig.children[c], formModel, formConfig);
         set.children.push(childSet);
      }
   }
   else if (logger.isLoggingEnabled())
   {
      logger.log("Ignoring set \"" + setConfig.setId + "\" as it does not have any fields or child sets.");
   }
   
   return set;
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
      
      var control = {};
      control.template = "controls/ambiguous.ftl";
      fieldDef = createTransientField(fieldName, control);
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
         fieldDef = createTransientField(fieldName, fieldConfig);
         
         if (fieldDef === null && logger.isLoggingEnabled())
         {
            logger.log("Ignoring field \"" + fieldName + "\" as neither a field definition or sufficient configuration could be located");
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
   // retrieve the default template for the field's data type
   if (fieldConfig !== null && fieldConfig.control !== null && fieldConfig.control.template !== null)
   {
      control.template = fieldConfig.control.template;
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
      var paramsConfig = defaultControlConfig.params;
      for (var p = 0; p < paramsConfig.length; p++)
      {
         control.params[paramsConfig[p].name] = paramsConfig[p].value;
      }
   }
   
   // get overridden control parameters (if there are any)
   if (fieldConfig !== null)
   {
      var paramsConfig = fieldConfig.control.params;
      for (var p = 0; p < paramsConfig.length; p++)
      {
         control.params[paramsConfig[p].name] = paramsConfig[p].value;
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
   if ((fieldDef.mandatory || fieldDef.endpointMandatory) && fieldDef.disabled == false)
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
      if (fieldConfig !== null && fieldConfig.constraintDefinitionMap[constraintId] !== null)
      {
         var fieldConstraintConfig = fieldConfig.constraintDefinitionMap[constraintId];
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
         // if the constraint is the list of values constraint, force the control
         // template to be select.ftl and setup the options
         fieldDef.control.template = "controls/select.ftl";
         
         // setup the options string and set as control params
         fieldDef.control.params.options = constraintParams.allowedValues;
      }
      else if (constraintId === "LENGTH")
      {
         // if the constraint is the length constraint, pass the maxLength
         // parameter to the control if appropriate
         if (typeof constraintParams.maxLength !== "undefined" && 
             constraintParams.maxLength != -1)
         {
            fieldDef.control.params.maxLength = constraintParams.maxLength;
         }
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
   
   if (typeof formModel.data.formData[fieldDef.dataKeyName] !== "undefined")
   {
      fieldDef.value = formModel.data.formData[fieldDef.dataKeyName];
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
   if (fieldConfig === null || fieldConfig.control === null || 
       fieldConfig.control.template === null)
   {
      return null;
   }
   
   if (logger.isLoggingEnabled())
      logger.log("Creating transient field for \"" + fieldName + "\"");
   
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
   fieldDef.control.template = fieldConfig.control.template;
   
   var params = {};
   if (typeof fieldConfig.params !== "undefined")
   {
      var paramsConfig = fieldConfig.params;
      for (var p = 0; p < paramsConfig.length; p++)
      {
         params[paramsConfig[p].name] = paramsConfig[p].value;
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
