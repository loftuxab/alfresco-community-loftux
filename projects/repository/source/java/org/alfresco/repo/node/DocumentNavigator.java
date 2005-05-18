/*
 * Created on 18-May-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.ref.ChildAssocRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.value.ValueConverter;
import org.jaxen.DefaultNavigator;
import org.jaxen.UnsupportedAxisException;
import org.jaxen.XPath;
import org.saxpath.SAXPathException;

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

    private NodeService nodeService; 
    
    private NamespaceService namespaceService;
    
    // Support classes to encapsulate stuff more akin to xml
    
    public class Property 
    {
        QName qname;
        Serializable value;
    }
    
    public class Namespace
    {
        String prefix;
        String uri;
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 3618984485740165427L;

    public DocumentNavigator(NodeService nodeService, NamespaceService namespaceService)
    {
        super();
        this.nodeService = nodeService;
        this.namespaceService = namespaceService;
    }

    @Override
    public String getAttributeName(Object o)
    {
        // Get the local name
        return ((Property)o).qname.getLocalName();
    }

    @Override
    public String getAttributeNamespaceUri(Object o)
    {
        return ((Property)o).qname.getNamespaceURI();
    }

    @Override
    public String getAttributeQName(Object o)
    {
        return ((Property)o).qname.toString();
    }

    @Override
    public String getAttributeStringValue(Object o)
    {
        // Only the first property of multi-valued properties is displayed
        // A multivalue attribute makes no sense in the xml world
        return ValueConverter.convert(String.class, ((Property)o).value);
    }

    @Override
    public String getCommentStringValue(Object o)
    {
        // There is no attribute that is a comment
        throw new UnsupportedOperationException("Comment string values are unsupported");
    }

    @Override
    public String getElementName(Object o)
    {
         return ((ChildAssocRef)o).getQName().getLocalName();
    }

    @Override
    public String getElementNamespaceUri(Object o)
    {
        return ((ChildAssocRef)o).getQName().getNamespaceURI();
    }

    @Override
    public String getElementQName(Object o)
    {
        return ((ChildAssocRef)o).getQName().toString();
    }

    @Override
    public String getElementStringValue(Object o)
    {
        throw new UnsupportedOperationException("Element string values are unsupported");
    }

    @Override
    public String getNamespacePrefix(Object o)
    {
        return ((Namespace)o).prefix;
    }

    @Override
    public String getNamespaceStringValue(Object o)
    {
        return ((Namespace)o).uri;
    }

    @Override
    public String getTextStringValue(Object o)
    {
      throw new UnsupportedOperationException("Text nodes are unsupported");
    }

    @Override
    public boolean isAttribute(Object o)
    {
       return (o instanceof Property);
    }

    @Override
    public boolean isComment(Object o)
    {
       return false;
    }

    @Override
    public boolean isDocument(Object o)
    {
       if(!(o  instanceof ChildAssocRef))
       {
           return false;
       }
       ChildAssocRef car = (ChildAssocRef)o;
       return (car.getParentRef() == null) && (car.getQName() == null);
    }

    @Override
    public boolean isElement(Object o)
    {
        return (o  instanceof ChildAssocRef);
    }

    @Override
    public boolean isNamespace(Object o)
    {
       return (o instanceof Namespace);
    }

    @Override
    public boolean isProcessingInstruction(Object o)
    {
        return false;
    }

    @Override
    public boolean isText(Object o)
    {
        return false;
    }

    @Override
    public XPath parseXPath(String o) throws SAXPathException
    {
        return new NodeServiceXPath(o, nodeService, namespaceService);
    }

    // Basic navigation support
    
    public Iterator getAttributeAxisIterator(Object o) throws UnsupportedAxisException
    {
        ArrayList<Property> properties = new ArrayList<Property>();
        Map<QName, Serializable> map = nodeService.getProperties(((ChildAssocRef)o).getChildRef());       
        for(QName qName : map.keySet())
        {
            // Do not support multi value attributes - return the first
            Property property = new Property();
            property.qname = qName;
            property.value = map.get(qName);
            properties.add(property);
        }
        return properties.iterator();
    }

    public Iterator getChildAxisIterator(Object o) throws UnsupportedAxisException
    {
        // Iterator of ChildAxisRef
        List<ChildAssocRef> list = nodeService.getChildAssocs(((ChildAssocRef)o).getChildRef());
        return list.iterator();
    }

    public Iterator getNamespaceAxisIterator(Object o) throws UnsupportedAxisException
    {
        // Iterator of Namespace
        ArrayList<Namespace> namespaces = new ArrayList<Namespace>();
        for(String prefix :namespaceService.getPrefixes())
        {
            String uri = namespaceService.getNamespaceURI(prefix);
            Namespace ns = new Namespace();
            ns.prefix = prefix;
            ns.uri = uri;
            namespaces.add(ns);
        }
        return namespaces.iterator();
    }

    public Iterator getParentAxisIterator(Object o) throws UnsupportedAxisException
    {
        ArrayList<ChildAssocRef> parents = new ArrayList<ChildAssocRef>(1);
        // Iterator of ??
        ChildAssocRef contextRef = (ChildAssocRef)o;
        if(contextRef.getParentRef() != null)
        {
           parents.add(nodeService.getPrimaryParent(contextRef.getParentRef()));
        }
        return parents.iterator();
    }

    public Object getDocumentNode(Object o)
    {
        return new ChildAssocRef(null, null, nodeService.getRootNode(((ChildAssocRef)o).getChildRef().getStoreRef()));
    }

    
    
}
