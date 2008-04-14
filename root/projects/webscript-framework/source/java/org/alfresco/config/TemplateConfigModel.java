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

import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;

import freemarker.ext.dom.NodeModel;

/**
 * Model representation of configuration for use in scripts.
 *
 * @author Gavin Cornwell
 */
public class TemplateConfigModel extends ConfigModel
{
   private NodeModel model;
   
   private static Log logger = LogFactory.getLog(TemplateConfigModel.class);

   /**
    * Constructor
    * 
    * @param configService ConfigService instance
    * @param scriptConfig The script's config as XML string
    */
   public TemplateConfigModel(ConfigService configService, String scriptConfig)
   {
      super(configService, scriptConfig);
      
      if (logger.isDebugEnabled())
         logger.debug(this.toString() + " created:\nconfig service: " + 
                  this.configService + "\nglobal config: " + this.globalConfig +
                  "\nscript config: " + this.scriptConfig);
   }
   
   /**
    * Returns the script's config as a Freemarker NodeModel object
    * 
    * @return Script config as a Freemarker NodeModel object
    */
   @Override
   public Object getScript()
   {
      if (this.model != null)
      {
         return this.model;
      }
      
      // if we get this far attempt to create the model, if we have 
      // something to create with
      if (this.scriptConfig != null && this.model == null)
      {
         StringReader reader = new StringReader(this.scriptConfig);
         InputSource is = new InputSource(reader);
         try
         {
            this.model = NodeModel.parse(is);
            return this.model;
         }
         catch (Exception e)
         {
            if (logger.isWarnEnabled())
               logger.warn("Failed to create 'script' config model: " + e.getMessage());
         }
      }
      
      // if we make it here return empty model
      return NodeModel.NOTHING;
   }
}
