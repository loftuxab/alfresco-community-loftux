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

import org.alfresco.jcr.session.SessionImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConverter;
import org.alfresco.service.namespace.QName;


/**
 * Responsible for converting Alfresco values to JCR values.
 * 
 * @author David Caruana
 *
 */
public class JCRTypeConverter
{
    private TypeConverter jcrTypeConverter;
    
    /**
     * Construct 
     * 
     * @param session
     */
    public JCRTypeConverter(SessionImpl session)
    {
        this.jcrTypeConverter = new SessionTypeConverter(session);
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
    public NodeImpl referenceValue(SessionImpl session, Object value) throws ValueFormatException, RepositoryException
    {
        try
        {
            NodeRef nodeRef = (NodeRef)jcrTypeConverter.convert(NodeRef.class, value);
            return new NodeImpl(session, nodeRef);
        }
        catch(Exception e)
        {
            translateException(e);
            throw new RepositoryException(e);
        }
    }
        
    /**
     * Convert to JCR String Value
     * 
     * @param value
     * @return
     * @throws ValueFormatException
     * @throws RepositoryException
     */
    public String stringValue(Object value) throws ValueFormatException, RepositoryException 
    {
        try
        {
            return (String)jcrTypeConverter.convert(String.class, value);
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
    public InputStream streamValue(Object value) throws IllegalStateException, RepositoryException
    {
        try
        {
            return (InputStream)jcrTypeConverter.convert(InputStream.class, value);
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
    public long longValue(Object value) throws ValueFormatException, IllegalStateException, RepositoryException
    {
        try
        {
            return jcrTypeConverter.longValue(value);
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
    public double doubleValue(Object value) throws ValueFormatException, IllegalStateException, RepositoryException
    {
        try
        {
            return jcrTypeConverter.doubleValue(value);
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
    public Calendar dateValue(Object value) throws ValueFormatException, IllegalStateException, RepositoryException
    {
        try
        {
            Date date = (Date)jcrTypeConverter.convert(Date.class, value);
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
    public boolean booleanValue(Object value) throws ValueFormatException, IllegalStateException, RepositoryException
    {
        try
        {
            return jcrTypeConverter.booleanValue(value);
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
        if (e instanceof UnsupportedOperationException ||
            e instanceof NumberFormatException)
        {
            throw new ValueFormatException(e);
        }
    }


    /**
     * Data Type Converter that takes into account JCR session context
     * 
     * @author David Caruana
     */
    private static class SessionTypeConverter extends TypeConverter
    {
        private SessionImpl session;
        
        /**
         * Construct
         * 
         * @param session  session context
         */
        public SessionTypeConverter(SessionImpl session)
        {
            this.session = session;
            
            /**
             * Converter for translating QName to string as prefix:localName 
             */
            addConverter(QName.class, String.class, new TypeConverter.Converter<QName, String>()
            {
                public String convert(QName source)
                {
                    return source.toPrefixString(SessionTypeConverter.this.session.getNamespaceResolver());
                }
            });
        }
        
        /* (non-Javadoc)
         * @see org.alfresco.service.cmr.repository.datatype.TypeConverter#getConverter(java.lang.Class, java.lang.Class)
         */
        @Override
        public <F, T> Converter getConverter(Class<F> source, Class<T> dest)
        {
            Converter converter = super.getConverter(source, dest);
            if (converter == null)
            {
                converter = DefaultTypeConverter.INSTANCE.getConverter(source, dest);
            }
            return converter;
        }
    }
    
}
