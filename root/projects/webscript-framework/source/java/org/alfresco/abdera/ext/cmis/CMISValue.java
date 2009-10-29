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

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.namespace.QName;

import org.alfresco.util.ISO8601DateFormat;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElementWrapper;


/**
 * CMIS Version: 0.61
 *
 * CMIS Property Value for the Abdera ATOM library.
 * 
 * @author davidc
 */
public class CMISValue extends ExtensibleElementWrapper
{
    /**
     * @param internal
     */
    public CMISValue(Element internal)
    {
        super(internal);
    }

    /**
     * @param factory
     * @param qname
     */
    public CMISValue(Factory factory, QName qname)
    {
        super(factory, qname);
    }

    /**
     * Gets property value (converting to appropriate Java type for the property type)
     * 
     * @return  property value (or null, if not specified)
     */
    public Object getNativeValue()
    {
        CMISProperty parent = (CMISProperty)getParentElement();
        String type = parent.getType();
        if (type.equals(CMISConstants.PROP_TYPE_STRING))
        {
            return getStringValue();
        }
        else if (type.equals(CMISConstants.PROP_TYPE_INTEGER))
        {
            return getIntegerValue();
        }
        else if (type.equals(CMISConstants.PROP_TYPE_DATETIME))
        {
            return getDateValue();
        }
        else if (type.equals(CMISConstants.PROP_TYPE_BOOLEAN))
        {
            return getBooleanValue();
        }
        else if (type.equals(CMISConstants.PROP_TYPE_DECIMAL))
        {
            return getDecimalValue();
        }
        // TODO: Handle remaining property types
        return getStringValue();
    }

    /**
     * Gets String value
     * 
     * @return  string value
     */
    public String getStringValue()
    {
        return getText();
    }

    /**
     * Gets Decimal value
     * 
     * @return  decimal value
     */
    public BigDecimal getDecimalValue()
    {
        return new BigDecimal(getStringValue());
    }

    /**
     * Gets Integer value
     * 
     * @return  integer value
     */
    public int getIntegerValue()
    {
        return new Integer(getStringValue());
    }

    /**
     * Gets Boolean value
     * 
     * @return  boolean value
     */
    public boolean getBooleanValue()
    {
        return Boolean.valueOf(getStringValue());
    }

    /**
     * Gets Date value
     * 
     * @return  date value
     */
    public Date getDateValue()
    {
        // TODO: Use mechanism not reliant on Alfresco code
        return ISO8601DateFormat.parse(getStringValue());
    }
    
}
