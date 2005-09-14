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

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.VersionException;

import org.alfresco.jcr.dictionary.DataTypeMap;
import org.alfresco.jcr.dictionary.PropertyDefinitionImpl;
import org.alfresco.jcr.proxy.JCRProxyFactory;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.namespace.QName;


/**
 * Alfresco implementation of a Property
 * 
 * @author David Caruana
 */
public class PropertyImpl extends ItemImpl implements Property
{

    private NodeImpl node;
    private QName name;
    private Property proxy = null;
    
    
    /**
     * Constructor
     *  
     * @param session
     */
    public PropertyImpl(NodeImpl node, QName name)
    {
        super(node.session);
        this.node = node;
        this.name = name;
    }

    /**
     * Create proxied JCR Property
     * 
     * @return  property
     */
    @Override
    public Property getProxy()
    {
        if (proxy == null)
        {
            proxy = (Property)JCRProxyFactory.create(this, Property.class, session); 
        }
        return proxy;
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(javax.jcr.Value)
     */
    public void setValue(Value value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(javax.jcr.Value[])
     */
    public void setValue(Value[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(java.lang.String)
     */
    public void setValue(String value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(java.lang.String[])
     */
    public void setValue(String[] values) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(java.io.InputStream)
     */
    public void setValue(InputStream value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(long)
     */
    public void setValue(long value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(double)
     */
    public void setValue(double value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(java.util.Calendar)
     */
    public void setValue(Calendar value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(boolean)
     */
    public void setValue(boolean value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#setValue(javax.jcr.Node)
     */
    public void setValue(Node value) throws ValueFormatException, VersionException, LockException, ConstraintViolationException, RepositoryException
    {
        throw new UnsupportedRepositoryOperationException();        
    }    
    
    /* (non-Javadoc)
     * @see javax.jcr.Property#getValue()
     */
    public Value getValue() throws ValueFormatException, RepositoryException
    {
        checkSingleValued();
        ValueImpl valueImpl = new ValueImpl(session, getType(), getPropertyValue());
        // TODO: Could consider returning proxied value implementation (but i don't think is necessary)
        return valueImpl;
    }

    public Value[] getValues() throws ValueFormatException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getString() throws ValueFormatException, RepositoryException
    {
        checkSingleValued();
        return JCRValue.stringValue(getPropertyValue());
    }

    public InputStream getStream() throws ValueFormatException, RepositoryException
    {
        checkSingleValued();
        return JCRValue.streamValue(getPropertyValue());
    }

    public long getLong() throws ValueFormatException, RepositoryException
    {
        checkSingleValued();
        return JCRValue.longValue(getPropertyValue());
    }

    public double getDouble() throws ValueFormatException, RepositoryException
    {
        checkSingleValued();
        return JCRValue.doubleValue(getPropertyValue());
    }

    public Calendar getDate() throws ValueFormatException, RepositoryException
    {
        checkSingleValued();
        return JCRValue.dateValue(getPropertyValue());
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#getBoolean()
     */
    public boolean getBoolean() throws ValueFormatException, RepositoryException
    {
        checkSingleValued();
        return JCRValue.booleanValue(getPropertyValue());
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#getNode()
     */
    public Node getNode() throws ValueFormatException, RepositoryException
    {
        checkSingleValued();
        return JCRValue.referenceValue(session, getPropertyValue());
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#getLength()
     */
    public long getLength() throws ValueFormatException, RepositoryException
    {
        checkSingleValued();
        return JCRValue.getLength(getPropertyValue());
    }

    public long[] getLengths() throws ValueFormatException, RepositoryException
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#getDefinition()
     */
    public PropertyDefinition getDefinition() throws RepositoryException
    {
        PropertyDefinitionImpl propDefImpl = new PropertyDefinitionImpl(session, getPropertyDefinition());
        return propDefImpl.getProxy();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Property#getType()
     */
    public int getType() throws RepositoryException
    {
        return DataTypeMap.convertDataTypeToPropertyType(getPropertyDefinition().getDataType());
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getName()
     */
    public String getName() throws RepositoryException
    {
        return name.toPrefixString(session.getNamespaceResolver());
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#isNode()
     */
    public boolean isNode()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getParent()
     */
    public Node getParent() throws ItemNotFoundException, AccessDeniedException, RepositoryException
    {
        return node;
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getPath()
     */
    public String getPath() throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        Path path = nodeService.getPath(node.getNodeRef());
        path.append(new JCRPath.SimpleElement(getName()));
        return path.toString();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getDepth()
     */
    public int getDepth() throws RepositoryException
    {
        NodeService nodeService = session.getServiceRegistry().getNodeService();
        Path path = nodeService.getPath(node.getNodeRef());
        // Note: Property is one depth lower than its node
        return path.size();
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#getAncestor(int)
     */
    public Item getAncestor(int depth) throws ItemNotFoundException, AccessDeniedException, RepositoryException
    {
        int propertyDepth = getDepth();
        if (depth < 0 || depth > propertyDepth)
        {
            throw new ItemNotFoundException("Ancestor at depth " + depth + " not found for property " + name);
        }

        if (depth == propertyDepth)
        {
            return this.getProxy();
        }
        else
        {
            return node.getAncestor(depth -1);
        }
    }

    /* (non-Javadoc)
     * @see javax.jcr.Item#isSame(javax.jcr.Item)
     */
    public boolean isSame(Item otherItem) throws RepositoryException
    {
        return getProxy().equals(otherItem);
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.Item#accept(javax.jcr.ItemVisitor)
     */
    public void accept(ItemVisitor visitor) throws RepositoryException
    {
        visitor.visit(getProxy());
    }
        
    /**
     * Gets the Node Implementation that contains this property
     * 
     * @return  the node implementation
     */
    protected NodeImpl getNodeImpl()
    {
        return node;
    }

    /**
     * Gets the Property Name
     * 
     * @return  the property name
     */
    protected QName getPropertyName()
    {
        return name;
    }
    
    /**
     * Gets the property value
     * 
     * @return  the property value
     */
    protected Object getPropertyValue()
        throws RepositoryException
    {
        Object value; 

        // TODO: Handle Content Property Type
        
        NodeService nodeService = node.session.getServiceRegistry().getNodeService();
        value = nodeService.getProperty(node.getNodeRef(), name);
        
        // TODO: Check - If value is now null, then effectively the property has been removed
        if (value == null)
        {
            throw new RepositoryException("Property " + name + " has been removed.");
        }
        return value;
    }
    
    /**
     * Checks that this property is single valued.
     * 
     * @throws ValueFormatException  if value is multi-valued
     */
    private void checkSingleValued()
        throws ValueFormatException
    {
        if (getPropertyDefinition().isMultiValued())
        {
            // Expected exception for JSR-170
            throw new ValueFormatException("Property " + name + " is multi-valued.");
        }
    }
    
    /**
     * Gets the Property Data Type
     * 
     * @return  the (JCR) data type
     */
    private org.alfresco.service.cmr.dictionary.PropertyDefinition getPropertyDefinition()
    {
        DictionaryService dictionary = session.getServiceRegistry().getDictionaryService();
        return dictionary.getProperty(name);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof PropertyImpl))
        {
            return false;
        }
        PropertyImpl other = (PropertyImpl)obj;
        return this.name.equals(other.name);
    }

    @Override
    public int hashCode()
    {
        return name.hashCode();
    }    

}
