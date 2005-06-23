/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.repo.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.jaxen.DefaultNavigator;
import org.jaxen.JaxenException;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;

/**
 * An implementation of the Jaxen xpath against the node service API
 * 
 * This means any node service can do xpath style navigation.
 * Given any context node we can navigate between nodes using xpath.
 * 
 * This allows simple path navigation and much more.
 * 
 * @author andyh
 *
 */
public class DocumentNavigator extends DefaultNavigator
{
    private static final long serialVersionUID = 3618984485740165427L;

    private DictionaryService dictionaryService;
    private NodeService nodeService;
    private NamespacePrefixResolver nspr;
    
    // Support classes to encapsulate stuff more akin to xml
    
    public class Property 
    {
        QName qname;
        Serializable value;
        NodeRef parent;
    }
    
    public class Namespace
    {
        String prefix;
        String uri;
    }
    
    private boolean followAllParentLinks;

    public DocumentNavigator(
            DictionaryService dictionaryService,
            NodeService nodeService,
            NamespacePrefixResolver nspr,
            boolean followAllParentLinks)
    {
        super();
        this.dictionaryService = dictionaryService;
        this.nodeService = nodeService;
        this.nspr = nspr;
        this.followAllParentLinks = followAllParentLinks;
    }

    public String getAttributeName(Object o)
    {
        // Get the local name
        return ((Property)o).qname.getLocalName();
    }

    public String getAttributeNamespaceUri(Object o)
    {
        return ((Property)o).qname.getNamespaceURI();
    }

    public String getAttributeQName(Object o)
    {
        return ((Property)o).qname.toString();
    }

    public String getAttributeStringValue(Object o)
    {
        // Only the first property of multi-valued properties is displayed
        // A multivalue attribute makes no sense in the xml world
        return ValueConverter.convert(String.class, ((Property)o).value);
    }

    public String getCommentStringValue(Object o)
    {
        // There is no attribute that is a comment
        throw new UnsupportedOperationException("Comment string values are unsupported");
    }

    public String getElementName(Object o)
    {
         return ((ChildAssociationRef)o).getQName().getLocalName();
    }

    public String getElementNamespaceUri(Object o)
    {
        return ((ChildAssociationRef)o).getQName().getNamespaceURI();
    }

    public String getElementQName(Object o)
    {
        return ((ChildAssociationRef)o).getQName().toString();
    }

    public String getElementStringValue(Object o)
    {
        throw new UnsupportedOperationException("Element string values are unsupported");
    }

    public String getNamespacePrefix(Object o)
    {
        return ((Namespace)o).prefix;
    }

    public String getNamespaceStringValue(Object o)
    {
        return ((Namespace)o).uri;
    }

    public String getTextStringValue(Object o)
    {
      throw new UnsupportedOperationException("Text nodes are unsupported");
    }

    public boolean isAttribute(Object o)
    {
       return (o instanceof Property);
    }

    public boolean isComment(Object o)
    {
       return false;
    }

    public boolean isDocument(Object o)
    {
       if(!(o  instanceof ChildAssociationRef))
       {
           return false;
       }
       ChildAssociationRef car = (ChildAssociationRef)o;
       return (car.getParentRef() == null) && (car.getQName() == null);
    }

    public boolean isElement(Object o)
    {
        return (o instanceof ChildAssociationRef);
    }
    
    public boolean isNamespace(Object o)
    {
       return (o instanceof Namespace);
    }

    public boolean isProcessingInstruction(Object o)
    {
        return false;
    }

    public boolean isText(Object o)
    {
        return false;
    }

    public XPath parseXPath(String o) throws JaxenException
    {
        return new NodeServiceXPath(o, dictionaryService, nodeService, nspr, null, followAllParentLinks);
    }

    // Basic navigation support
    
    public Iterator getAttributeAxisIterator(Object o) throws UnsupportedAxisException
    {
        ArrayList<Property> properties = new ArrayList<Property>();
        NodeRef nodeRef = ((ChildAssociationRef)o).getChildRef();
        Map<QName, Serializable> map = nodeService.getProperties(nodeRef);       
        for(QName qName : map.keySet())
        {
            // Do not support multi value attributes - return the first
            Property property = new Property();
            property.qname = qName;
            property.value = map.get(qName);
            property.parent = nodeRef;
            properties.add(property);
        }
        return properties.iterator();
    }

    public Iterator getChildAxisIterator(Object o) throws UnsupportedAxisException
    {
        // Iterator of ChildAxisRef
        ChildAssociationRef assocRef = (ChildAssociationRef) o;
        NodeRef childRef = assocRef.getChildRef();
        List<ChildAssociationRef> list = nodeService.getChildAssocs(childRef);
        return list.iterator();
    }

    public Iterator getNamespaceAxisIterator(Object o) throws UnsupportedAxisException
    {
        // Iterator of Namespace
        ArrayList<Namespace> namespaces = new ArrayList<Namespace>();
        for(String prefix : nspr.getPrefixes())
        {
            String uri = nspr.getNamespaceURI(prefix);
            Namespace ns = new Namespace();
            ns.prefix = prefix;
            ns.uri = uri;
            namespaces.add(ns);
        }
        return namespaces.iterator();
    }

    public Iterator getParentAxisIterator(Object o) throws UnsupportedAxisException
    {
        ArrayList<ChildAssociationRef> parents = new ArrayList<ChildAssociationRef>(1);
        // Iterator of ??
        ChildAssociationRef contextRef = (ChildAssociationRef)o;
        if(contextRef.getParentRef() != null)
        {
            if(followAllParentLinks)
            {
                for(ChildAssociationRef car: nodeService.getParentAssocs(contextRef.getChildRef()))
                {
                   parents.add(nodeService.getPrimaryParent(car.getParentRef()));
                }
            }
            else
            {
               parents.add(nodeService.getPrimaryParent(contextRef.getParentRef()));
            }
        }
        return parents.iterator();
    }

    public Object getDocumentNode(Object o)
    {
        ChildAssociationRef assocRef = (ChildAssociationRef) o;
        StoreRef storeRef = assocRef.getChildRef().getStoreRef();
        return new ChildAssociationRef(null, null, null, nodeService.getRootNode(storeRef));
    }

    public Object getNode(NodeRef nodeRef)
    {
        return nodeService.getPrimaryParent(nodeRef);
    }

    public Boolean like(NodeRef childRef, QName qname, String sqlLikePattern, boolean includeFTS)
    {
       return nodeService.like(childRef, qname, sqlLikePattern, includeFTS);
    }
    
    public Boolean contains(NodeRef childRef, QName qname, String sqlLikePattern)
    {
       return nodeService.contains(childRef, qname, sqlLikePattern);
    }
    
    public Boolean isSubtypeOf(NodeRef nodeRef, QName typeQName)
    {
        // get the type of the node
        QName nodeTypeQName = nodeService.getType(nodeRef);
        return dictionaryService.isSubClass(nodeTypeQName, typeQName);
    }
}
