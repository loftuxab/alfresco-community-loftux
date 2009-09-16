/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */

/**
 * Mandatory validation handler, tests that the given field has a value.
 * 
 * @method mandatory
 * @param field {object} The element representing the field the validation is for
 * @param args {object} Not used
 * @param event {object} The event that caused this handler to be called, maybe null
 * @param form {object} The forms runtime class instance the field is being managed by
 * @param silent {boolean} Determines whether the user should be informed upon failure
 * @param message {string} Message to display when validation fails, maybe null
 * @static
 */
Alfresco.forms.validation.rmVitalRecordPeriodMandatory = function rmVitalRecordPeriodMandatory(field, args, event, form, silent, message)
{
   if (Alfresco.logger.isDebugEnabled())
      Alfresco.logger.debug("Validating mandatory state of vital record period '" + field.id + "'");
   
   // if the vital record indicator checkbox is checked the period must 
   // be set to a valid period.
   var form = YAHOO.util.Dom.get(form.formId);
   var vitalRecordIndicator = form["prop_rma_vitalRecordIndicator"];
   if ((typeof vitalRecordIndicator !== undefined) && vitalRecordIndicator !== null)
   {
      if (vitalRecordIndicator.value === "true")
      {
         // if the vital record indicator is checked ensure that a value
         // has been set and that "None" is not selected
         var fieldValue = YAHOO.lang.trim(field.value);
         var valid = (fieldValue.length !== 0) && (fieldValue !== "none") && (fieldValue !== "none|0");
         
         if (Alfresco.logger.isDebugEnabled())
            Alfresco.logger.debug("Checked vital record period value as vital record indicator is true, period value: " + valid);
         
         return valid;
      }
   }
   
   // if we get this far just do the normal mandatory check
   return Alfresco.forms.validation.mandatory(field, args, event, form, silent, message);
};

