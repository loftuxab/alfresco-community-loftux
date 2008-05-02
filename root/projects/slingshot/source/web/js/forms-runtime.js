/* Ensure Alfresco.forms and validation objects exist */
Alfresco.forms = Alfresco.forms || {};
Alfresco.forms.validation = Alfresco.forms.validation || {};

/*
 *** Alfresco.forms.Form
*/
(function()
{
   /* Form Definition */
   
   Alfresco.forms.Form = function(formId)
   {
      this.formId = formId;
      this.submitIds = null;
      this.validateOnSubmit = true;
      this.errorPlacement = "alert";
      this.ajaxSubmit = false;
      this.validations = [];
      
      var form = document.getElementById(this.formId);
      if (form != null)
      {
         // add the event to the form and make the scope of the handler this form.
         YAHOO.util.Event.addListener(form, "submit", this.submitInvoked, this, true);
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Added submit handler for form: ", this.formId);
      }
      else
      {
         this.showInternalError("form with id of '" + this.formId + 
               "' could not be located, ensure the form is created after the form element is available.");
      }
      
      return this;
   };
   
   Alfresco.forms.Form.prototype =
   {
      formId: null,
      submitIds: null,
      validateOnSubmit: null,
      errorPlacement: null,
      ajaxSubmit: null,
      validations: null,

      // ****************
      //  Public methods
      // ****************
        
      addValidation: function(fieldId, validationHandler, validationArgs, when)
      {
         var field = document.getElementById(fieldId);
         if (field == null)
         {
            this.showInternalError("element with id of '" + fieldId + "' could not be located.");
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
            YAHOO.util.Event.addListener(field, when, this.validationEventFired, validation, this);
            
            if (Alfresco.logger.isDebugEnabled())
               Alfresco.logger.debug("Added validation for field: ", validation);
         }
      },
      
      // ******************
      //  Private methods
      // ******************
      
      validationEventFired: function(event, validation)
      {
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Validation event has been fired for field: " + validation.fieldId);
         
         // TODO: run all handlers to see if disabled buttons can be enabled, if mode allows!
         
         // call handler
         validation.handler(YAHOO.util.Event.getTarget(event), validation.args, this);
      },
      
      submitInvoked: function(event)
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
               // TODO: revisit this as we will want to collect all
               //       the errors when errorPlacement is not 'alert'
               if (val.handler(field, val.args, this) == false)
               {
                  // stop the event from continuing and sending the form.
                  YAHOO.util.Event.stopEvent(event);
               }
            }
         }
      },
      
      showInternalError: function(msg, field)
      {
         this.addError("Internal Form Error: " + msg, field, true);
      },
      
      addError: function(msg, field, showNow)
      {
         // TODO: Put the error in the appropriate place, according
         //       to the 'errorPlacement' setting
         
         if (showNow)
         {
            alert(msg);
         }
      }
   };
   
   /* Validation Handlers */
   
   Alfresco.forms.validation.length = function(field, args, form)
   {
      if (Alfresco.logger.isDebugEnabled())
         Alfresco.logger.debug("Validating length of field with id '" + field.id +
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
   
})();