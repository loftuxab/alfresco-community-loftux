/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.jcr.item;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.jcr.session.SessionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;


/**
 * Responsible for converting Alfresco values to JCR values.
 * 
 * @author David Caruana
 *
 */
public class JCRValue
{

    /**
     * Get Length of a Value
     * 
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws RepositoryException
     */
    public static long getLength(Object value) throws ValueFormatException, RepositoryException
    {
        // TODO: Handle streams
        
        String strValue = (String)ValueConverter.convert(String.class, value);
        return strValue.length();
    }
    
    /**
     * Convert to JCR Reference Value
     * 
     * @param session
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws RepositoryException
     */
    public static NodeImpl referenceValue(SessionImpl session, Object value) throws ValueFormatException, RepositoryException
    {
        if (value instanceof NodeRef)
        {
            return new NodeImpl(session, (NodeRef)value);
        }
        else if (value instanceof String)
        {
            try
            {
                return new NodeImpl(session, new NodeRef((String)value));
            }
            catch(AlfrescoRuntimeException e)
            {
                throw new ValueFormatException("Node Reference " + value + " is invalid.");
            }
        }
        throw new ValueFormatException("Cannot convert value to Reference.");
    }
        
    /**
     * Convert to JCR String Value
     * 
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws RepositoryException
     */
    public static String stringValue(Object value) throws ValueFormatException, RepositoryException 
    {
        try
        {
            return (String)ValueConverter.convert(String.class, value);
        }
        catch(Exception e)
        {
            translateException(e);
            throw new RepositoryException(e);
        }
    }

    /**
     * Convert to JCR Stream Value
     * 
     * @param value
     * @return
     * @throws IllegalStateException
     * @throws RepositoryException
     */
    public static InputStream streamValue(Object value) throws IllegalStateException, RepositoryException
    {
        try
        {
            return (InputStream)ValueConverter.convert(InputStream.class, value);
        }
        catch(Exception e)
        {
            translateException(e);
            throw new RepositoryException(e);
        }
    }

    /**
     * Convert to JCR Long Value
     * 
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws IllegalStateException
     * @throws RepositoryException
     */
    public static long longValue(Object value) throws ValueFormatException, IllegalStateException, RepositoryException
    {
        try
        {
            return ValueConverter.longValue(value);
        }
        catch(Exception e)
        {
            translateException(e);
            throw new RepositoryException(e);
        }
    }

    /**
     * Convert to JCR Double Value
     * 
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws IllegalStateException
     * @throws RepositoryException
     */
    public static double doubleValue(Object value) throws ValueFormatException, IllegalStateException, RepositoryException
    {
        try
        {
            return ValueConverter.doubleValue(value);
        }
        catch(Exception e)
        {
            translateException(e);
            throw new RepositoryException(e);
        }
    }

    /**
     * Convert to JCR Date Value
     * 
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws IllegalStateException
     * @throws RepositoryException
     */
    public static Calendar dateValue(Object value) throws ValueFormatException, IllegalStateException, RepositoryException
    {
        try
        {
            Date date = (Date)ValueConverter.convert(Date.class, value);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        }
        catch(Exception e)
        {
            translateException(e);
            throw new RepositoryException(e);
        }
    }

    /**
     * Convert to JCR Boolean Value
     * 
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws IllegalStateException
     * @throws RepositoryException
     */
    public static boolean booleanValue(Object value) throws ValueFormatException, IllegalStateException, RepositoryException
    {
        try
        {
            return ValueConverter.booleanValue(value);
        }
        catch(Exception e)
        {
            translateException(e);
            throw new RepositoryException(e);
        }
    }


    /**
     * Catch and translate value conversion errors
     * 
     * @param e  exception to translate
     * @throws ValueFormatException  value formatting exception
     */
    private static void translateException(Exception e) throws ValueFormatException 
    {
        if (e instanceof UnsupportedOperationException)
        {
            throw new ValueFormatException(e);
        }
    }

}
