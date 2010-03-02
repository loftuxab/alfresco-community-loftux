/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Console template.
 *
 * @namespace Alfresco
 * @class Alfresco.ConsoleTemplate
 */
(function()
{
   /**
    * Console constructor.
    *
    * @return {Alfresco.ConsoleTemplate} The new ConsoleTemplate instance
    * @constructor
    */
   Alfresco.ConsoleTemplate = function ConsoleTemplate_constructor()
   {
      Alfresco.ConsoleTemplate.superclass.constructor.call(this);
      
      return this;
   };

   YAHOO.extend(Alfresco.ConsoleTemplate, Alfresco.widget.Resizer,
   {
      /**
       * DOM ID of left-hand container DIV
       *
       * @property divLeft
       * @type string
       * @default "divLeft"
       */
      divLeft: "divConsoleTools",

      /**
       * DOM ID of right-hand container DIV
       *
       * @property divRight
       * @type string
       * @default "divRight"
       */
      divRight: "divConsoleMain"
   });

})();

// Instantiate the Console template
new Alfresco.ConsoleTemplate();