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
 * Wiki template.
 * 
 * @namespace Alfresco
 * @class Alfresco.Wiki
 */
(function()
{
   /**
    * Wiki constructor.
    * 
    * @return {Alfresco.Wiki} The new Wiki instance
    * @constructor
    */
   Alfresco.Wiki = function Wiki_constructor()
   {
      Alfresco.Wiki.superclass.constructor.call(this);
      return this;
   };
   
   YAHOO.extend(Alfresco.Wiki, Alfresco.widget.Resizer,
   {
      /**
       * DOM ID of left-hand container DIV
       *
       * @property divLeft
       * @type string
       * @default "divLeft"
       */
      divLeft: "divTopicListFilters",

      /**
       * DOM ID of right-hand container DIV
       *
       * @property divRight
       * @type string
       * @default "divRight"
       */
      divRight: "divTopicListTopics"
   });
   
})();

// Instantiate the Wiki template
new Alfresco.Wiki();