function main()
{
   var PROP_PREFIX = "prop:", ASSOC_PREFIX = "assoc:";
   
   var formModel = null;
   var formUIModel = null;
   
   if (context.properties.nodeRef != null && context.properties.nodeRef != "")
   {
      var nodeRef = context.properties.nodeRef;
      logger.log("nodeRef = " + nodeRef);
      
      var json = remote.call("/api/forms/node/" + nodeRef.replace(":/", ""));
      
      if (logger.isLoggingEnabled())
      {
         logger.log("json = " + json);
      }
      
      formModel = eval('(' + json + ')');
         
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
         
         // get the default controls to use
         var defaultControls = config.global["default-controls"];
         
         // get the default constraint handlers to use
         var defaultConstraintHandlers = config.global["constraint-handlers"];
         
         // iterate over fields array and cache the properties and associations
         // separately for quick access below.
         var propertyFields = {};
         var associationFields = {};
   
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
            
            // find the appropriate field definition for the field, if the configuration
            // is ambiguous show a warning for admins and use the a control template to warn users
            var fieldDef = null;
            var propFieldDef = propertyFields[fieldName];
            var assocFieldDef = associationFields[fieldName];
            
            if (typeof propFieldDef !== "undefined" && typeof assocFieldDef !== "undefined")
            {
               logger.log("WARN: \"" + fieldName + "\" is ambiguous, a property and an association exists with this name, prefix with either 'prop:' or 'assoc:' to uniquely identify the field");
               fieldDef = {};
               fieldDef.name = fieldName;
               fieldDef.type = "field";
               fieldDef.id = fieldDef.name.replace(":", "_");
               fieldDef.control = {};
               fieldDef.control.template = "controls/ambiguous.ftl";
               formUIItems.push(fieldDef);
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
                  fieldDef.name = (fieldDef.type === "property") ? PROP_PREFIX + fieldName : ASSOC_PREFIX + fieldName;
                  fieldDef.id = fieldDef.name.replace(/:/g, "_");
                  
                  // construct the control object for the field
                  var control = {};
                  control.params = {};
                  var defaultControlConfig = null;
                  
                  // see if the fieldConfig already has a template defined, if not 
                  // retrive the default template for the field's data type
                  if (typeof fieldConfig.template === "undefined")
                  {
                     if (fieldDef.type === "property")
                     {
                        // get the default control for the property data type
                        defaultControlConfig = defaultControls.items[fieldDef.dataType];
                        
                        if (defaultControlConfig !== null)
                        {
                           control.template = defaultControlConfig.template;
                        }
                        else
                        {
                           logger.log("WARN: no default control found for data type \"" + fieldDef.dataType + "\"");
                        }
                     }
                     else if (fieldDef.type === "association")
                     {
                        // get the default control for associations
                        defaultControlConfig = defaultControls.items["association"];
                        
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
                  else
                  {
                     control.template = fieldConfig.template;
                  }
                  
                  // setup the control parameters, first get the parameters for the 
                  // default control for the field's data type, then get the parameters
                  // defined for the field itself
                  if (defaultControlConfig !== null)
                  {
                     var paramsConfig = defaultControlConfig.controlParams;
                     for (var p = 0; p < paramsConfig.size(); p++)
                     {
                        control.params[paramsConfig.get(p).name] = paramsConfig.get(p).value;
                     }
                  }
                  // TODO: get overridden control parameters
                  
                  fieldDef.control = control;
                  
                  // TODO: override field definition with config
                  //       i.e. read-only, label, help text, disabled etc.
                  
                  // set the value for the field
                  fieldDef.value = "";
                  if (typeof formModel.data.formData[fieldDef.name] !== "undefined")
                  {
                     fieldDef.value = formModel.data.formData[fieldDef.name];
                  }
                  
                  formUIItems.push(fieldDef);
               }
               else
               {
                  logger.log("Ignoring field '" + fieldName + "' as a field definition could not be located");
               }
            }
         }
      }
      
      // TODO: deal with 'sets', the fields must be within their appropriate set
      //       and structured with the correct hierarchy, should this be done in
      //       here or is it up to the config service to determine the hierarchy?
      
      formUIModel.items = formUIItems;
   }
   
   // dump the form UI model when debug is active
   if (logger.isLoggingEnabled())
   {
      var msg = "null";
      if (formUIModel != null)
      {
         msg = jsonUtils.toJSONString(formUIModel)
      }
      logger.log("formUIModel = " + msg);
   }
   
   // pass form ui model to FTL
   model.form = formUIModel;
}

main();
