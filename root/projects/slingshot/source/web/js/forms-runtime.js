// Ensure Alfresco.forms and validation objects exist
Alfresco.forms = Alfresco.forms || {};
Alfresco.forms.validation = Alfresco.forms.validation || {};

(function()
{
   
   Alfresco.forms.Form = function(formId)
   {
      this.formId = formId;
      this.submitIds = null;
      this.validateOnSubmit = true;
      this.ajaxSubmit = false;
      this.validations = [];
      
      return this;
   };
   
   Alfresco.forms.Form.prototype =
   {
      formId: null,
      submitIds: null,
      validateOnSubmit: null,
      ajaxSubmit: null,
      validations: null,
      
      init: function()
      {
         // TODO: determine what event handlers need to be setup depending on the
         //       current state of the form object.
      
         var form = document.getElementById(this.formId);
         if (form != null)
         {
            // add the event to the form and make the scope of the handler this form.
            YAHOO.util.Event.addListener(form, "submit", this._submitInvoked, this, true);
            
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Added submit handler for form: ", this.formId);
         }
         else
         {
            this._showInternalError("form with id of '" + this.formId + 
                  "' could not be located, ensure the form is created after the form element is available.");
         }
      },
      
      validateOnSubmit: function(validate)
      {
         alert("not implemented yet");
      },
      
      setSubmitIds: function(submitIds)
      {
         alert("not implemented yet");
      },
      
      enableAJAXSubmit: function(callbacks)
      {
         alert("not implemented yet");
      },
      
      addValidation: function(fieldId, validationHandler, validationArgs, when)
      {
         var field = document.getElementById(fieldId);
         if (field == null)
         {
            this._showInternalError("element with id of '" + fieldId + "' could not be located.");
            return;
         }
         
         // create object representation of validation
         var validation = {
            fieldId: fieldId,
            args: validationArgs,
            handler: validationHandler
         };
         
         // add to list of validations
         this.validations.push(validation);
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Added validation for submit: ", validation);
      
         // if an event has been specified attach an event handler
         if (when != null && when.length > 0)
         {
            // add the event to the field, pass the validation as a paramter 
            // to the handler and make the scope of the handler this form.
            YAHOO.util.Event.addListener(field, when, this._validationEventFired, validation, this);
            
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Added validation for field: ", validation);
         }
      },
      
      addError: function(msg, field, showNow)
      {
         // TODO: Add the error next to the field if supplied otherwise
         //       at the top of the form
         
         if (showNow)
         {
            alert(msg);
         }
      },
      
      _validationEventFired: function(event, validation)
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Validation event has been fired for field: " + validation.fieldId);
         
         // TODO: run all handlers to see if disabled buttons can be enabled, if mode allows!
         
         // call handler
         validation.handler(YAHOO.util.Event.getTarget(event), validation.args, this);
      },
      
      _submitInvoked: function(event)
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Submit invoked on form: ", this);
         
         // iterate through the validations
         for (var x = 0; x < this.validations.length; x++)
         {
            var val = this.validations[x];
            
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Invoking validation handler: ", val);
                  
            var field = document.getElementById(val.fieldId);
            if (field != null)
            {
               if (val.handler(field, val.args, this) == false)
               {
                  // stop the event from continuing and sending the form.
                  YAHOO.util.Event.stopEvent(event);
                  break;
               }
            }
         }
      },
      
      _showInternalError: function(msg, field)
      {
         this.addError("Internal Form Error: " + msg, field, true);
      }
   };
   
   /* Validation Handlers */
   
   Alfresco.forms.validation.mandatory = function(field, args, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating mandatory state of field '" + field.id + "'");
      
      var valid = true; 
         
      if (field.type && field.type == "radio")
      {
         // TODO: Do we actually need to support this scenario?
         //       wouldn't a radio button normally have a default
         //       'checked' option?
         
         var formElem = document.getElementById(form.formId);
         var radios = formElem[field.name];
         var anyChecked = false;
         for (var x = 0; x < radios.length; x++)
         {
            if (radios[x].checked)
            {
               anyChecked = true;
               break;
            }
         }
         
         valid = anyChecked;
      }
      else
      {
         valid = field.value.length != 0;
      }
      
      if (valid == false)
      {
         form.addError(field.id + " is mandatory.", field, true);
      }
      
      return valid; 
   };
   
   Alfresco.forms.validation.length = function(field, args, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating length of field '" + field.id +
                               "' using args: " + YAHOO.lang.dump(args));
      
      var valid = true;
      var min = -1;
      var max = -1;
      
      if (args.min)
      {
         min = args.min;
      }
      
      if (args.max)
      {
         max = args.max;
      }
      
      var length = field.value.length;
      
      if (min != -1 && length < min)
      {
         valid = false;
      }
      
      if (max != -1 && length > max)
      {
         valid = false;
      }
      
      if (valid == false)
      {
         form.addError(field.id + " is not the correct length.", field, true);
      }
      
      return valid;
   };
   
   Alfresco.forms.validation.number = function(field, args, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating field '" + field.id + "' is a number");
      
      var valid = (isNaN(field.value) == false);
      
      if (valid == false)
      {
         form.addError(field.id + " is not a number.", field, true);
      }
      
      return valid;
   };
   
   Alfresco.forms.validation.numberRange = function(field, args, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating number range of field '" + field.id +
                               "' using args: " + YAHOO.lang.dump(args));
      
      var valid = true;
      var value = field.value;
      
      if (value.length > 0)
      {
         if (isNaN(value))
         {
            valid = false;
            form.addError(field.id + " is not a number.", field, true);
         }
         else
         {
            var min = -1;
            var max = -1;
            
            if (args.min)
            {
               min = args.min;
            }
            
            if (args.max)
            {
               max = args.max;
            }
            
            if (min != -1 && value < min)
            {
               valid = false;
            }
            
            if (max != -1 && length > max)
            {
               valid = false;
            }
            
            if (valid == false)
            {
               form.addError(field.id + " is not within the allowable range.", field, true);
            }
         }
      }
      
      return valid;
   };
   
   Alfresco.forms.validation.regexMatch = function(field, args, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating regular expression of field '" + field.id +
                               "' using args: " + YAHOO.lang.dump(args));
      
      var valid = true;
      
      if (field.value.length > 0)
      {
         var pattern = new RegExp(args.pattern);
         valid = pattern.test(field.value);
         
         if (valid == false)
         {
            form.addError(field.id + " is invalid.", field, true);
         }
      }
      
      return valid;
   };
   
})();