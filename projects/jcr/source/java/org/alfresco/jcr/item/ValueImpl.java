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

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.alfresco.jcr.proxy.JCRProxyFactory;
import org.alfresco.jcr.session.SessionImpl;


/**
 * Alfresco implementation of JCR Value
 * 
 * @author David Caruana
 */
public class ValueImpl implements Value
{
    private enum ValueState {Stream, Value, None};
    private ValueState state = ValueState.None;
    
    private SessionImpl session;
    private int datatype;
    private Object value;
    private InputStream stream = null;
    
    private Value proxy;
    

    /**
     * Constuct
     * 
     * @param value  value to wrap
     */
    public ValueImpl(SessionImpl session, int datatype, Object value)
    {
        this.session = session;
        this.datatype = datatype;
        this.value = value;
    }
    
    /**
     * Create a proxied JCR Value
     * 
     * @return  the proxied value
     */
    public Value getProxy()
    {
        if (proxy == null)
        {
            proxy = (Value)JCRProxyFactory.create(this, Value.class, session); 
        }
        return proxy;
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.Value#getString()
     */
    public String getString() throws ValueFormatException, IllegalStateException, RepositoryException
    {
        enterState(ValueState.Value);
        return JCRValue.stringValue(value);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Value#getStream()
     */
    public InputStream getStream() throws IllegalStateException, RepositoryException
    {
        enterState(ValueState.Stream);
        if (stream == null)
        {
            stream = JCRValue.streamValue(value);
        }
        return stream;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Value#getLong()
     */
    public long getLong() throws ValueFormatException, IllegalStateException, RepositoryException
    {
        enterState(ValueState.Value);
        return JCRValue.longValue(value);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Value#getDouble()
     */
    public double getDouble() throws ValueFormatException, IllegalStateException, RepositoryException
    {
        enterState(ValueState.Value);
        return JCRValue.doubleValue(value);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Value#getDate()
     */
    public Calendar getDate() throws ValueFormatException, IllegalStateException, RepositoryException
    {
        enterState(ValueState.Value);
        return JCRValue.dateValue(value);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Value#getBoolean()
     */
    public boolean getBoolean() throws ValueFormatException, IllegalStateException, RepositoryException
    {
        enterState(ValueState.Value);
        return JCRValue.booleanValue(value);
    }

    /* (non-Javadoc)
     * @see javax.jcr.Value#getType()
     */
    public int getType()
    {
        return datatype;
    }
    
    /**
     * Enter a new value state
     * 
     * @param state  the new state to enter
     * @throws IllegalStateException  cannot enter new state
     */
    private void enterState(ValueState state)
    {
        if (this.state != ValueState.None && this.state != state)
        {
            throw new IllegalStateException("This value has already been retrieved as a " + state + " and cannot be retrieved as a " + ValueState.Stream + ".");
        }
        this.state = state;
    }

}
