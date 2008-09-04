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
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.web.scripts.atom;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.abdera.factory.ExtensionFactory;
import org.springframework.beans.factory.InitializingBean;


/**
 * Abdera Extension.
 * 
 * Mechanism for registering Atom extensions including QNames and
 * Factories for managing the retrieval and setting of extensions.
 * 
 * @author davidc
 */
public class AbderaExtension implements InitializingBean
{
    private AbderaServiceImpl abderaService;
    private String defaultNamespace = null;
    private Map<String, String> qnames = null;
    private List<ExtensionFactory> extensionFactories = null;
    
    /**
     * @param abderaService
     */
    public void setAbderaService(AbderaServiceImpl abderaService)
    {
        this.abderaService = abderaService;
    }
    
    /**
     * @param defaultNamespace
     */
    public void setDefaultNamespace(String defaultNamespace)
    {
        this.defaultNamespace = defaultNamespace;
    }

    /**
     * @param qnames
     */
    public void setQNames(Map<String, String> qnames)
    {
        this.qnames = qnames;
    }

    /**
     * @param extensionFactories
     */
    public void setExtensionFactories(List<ExtensionFactory> extensionFactories)
    {
        this.extensionFactories = extensionFactories;
    }

    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
        throws Exception
    {
        if (abderaService == null)
        {
            throw new AlfrescoRuntimeException("Abdera Service not specified");
        }
        
        // register qname extensions
        if (qnames != null)
        {
            for (Entry<String, String> entry : qnames.entrySet())
            {
                String qname = entry.getValue();
                if (defaultNamespace != null && qname.indexOf('{') == -1)
                {
                    qname = "{" + defaultNamespace + "}" + qname;
                }
                abderaService.registerQName(entry.getKey(), qname);
            }
        }
        
        // register extension factories
        if (extensionFactories != null)
        {
            for (ExtensionFactory extensionFactory : extensionFactories)
            {
                abderaService.registerExtensionFactory(extensionFactory);
            }
        }
    }

}
