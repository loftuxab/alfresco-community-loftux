/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.web.page;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.config.Config;
import org.alfresco.connector.RemoteClient;
import org.alfresco.web.config.RemoteConfigElement;
import org.alfresco.web.config.RemoteConfigElement.EndpointDescriptor;
import org.alfresco.web.scripts.PresentationContainer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kevin Roast
 */
public class PageRendererRuntimeContainer extends PresentationContainer
{
   private static Log logger = LogFactory.getLog(PresentationContainer.class);

   private ThreadLocal<String> ticket = new ThreadLocal<String>();
   
   void setTicket(String ticket)
   {
      this.ticket.set(ticket);
   }
   
   /**
    * Build the script root objects - add the ScriptRemote for remote HTTP API calls.
    */
   @Override
   public Map<String, Object> getScriptParameters()
   {
      // NOTE: returns unmodifable map from super
      Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
      params.putAll(super.getScriptParameters());
      
      // retrieve remote server configuration
      String endpoint = null;
      Config config = getConfigService().getConfig("Remote");
      if (config != null)
      {
         RemoteConfigElement remoteConfig = (RemoteConfigElement)config.getConfigElement("remote");
         
         // assume the default endpoint for now
         String endpointId = remoteConfig.getDefaultEndpointId();
         EndpointDescriptor descriptor = remoteConfig.getEndpointDescriptor(endpointId);
         if (descriptor != null)
         {
        	endpoint = descriptor.getEndpointUrl();
         }
         else
         {
            logger.warn("No 'endpoint' configured for ScriptRemote HTTP API access - remote object not available!");
         }
         
         // use appropriate webscript servlet here - one that supports TICKET param auth
         RemoteClient remote = new RemoteClient(endpoint, "UTF-8");
         remote.setTicket(ticket.get());

         params.put("remote", remote);
      }

      return params;
   }
}
