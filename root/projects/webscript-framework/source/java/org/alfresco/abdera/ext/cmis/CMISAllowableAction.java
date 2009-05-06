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
package org.alfresco.abdera.ext.cmis;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ElementWrapper;


/**
 * CMIS Version: 0.61
 *
 * CMIS Allowable Action for the Abdera ATOM library.
 * 
 * @author davidc
 */
public abstract class CMISAllowableAction extends ElementWrapper
{
    /**
     * @param internal
     */
    public CMISAllowableAction(Element internal)
    {
        super(internal);
    }

    /**
     * @param factory
     * @param qname
     */
    public CMISAllowableAction(Factory factory, QName qname)
    {
        super(factory, qname);
    }

    /**
     * Gets the Allowable Action name
     * 
     * @return  name
     */
    public String getName()
    {
        return getQName().getLocalPart();
    }
    
    /**
     * Is the Action Allowed
     * 
     * @return
     */
    public boolean isAllowed()
    {
        return Boolean.getBoolean(this.getText());
    }
    
}
