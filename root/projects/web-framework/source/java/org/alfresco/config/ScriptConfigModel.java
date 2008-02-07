/*
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Model representation of configuration for use in scripts.
 * 
 * @author Gavin Cornwell
 */
public class ScriptConfigModel extends ConfigModel
{
   private static Log logger = LogFactory.getLog(ScriptConfigModel.class);
   
   /**
    * Constructor
    * 
    * @param configService ConfigService instance
    * @param scriptConfig The script's config as XML string
    */
   public ScriptConfigModel(ConfigService configService, String scriptConfig)
   {
      super(configService, scriptConfig);
      
      if (logger.isDebugEnabled())
         logger.debug(this.toString() + " created:\nconfig service: " + 
                  this.configService + "\nglobal config: " + this.globalConfig +
                  "\nscript config: " + this.scriptConfig);
   }

   /**
    * Returns the script's config as a String
    * 
    * @return Script config as a String
    */
   @Override
   public Object getScript()
   {
      return this.scriptConfig;
   }
}
