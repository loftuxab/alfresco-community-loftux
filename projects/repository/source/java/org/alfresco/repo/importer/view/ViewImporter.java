package org.alfresco.repo.importer.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.alfresco.repo.importer.Importer;
import org.alfresco.repo.importer.ImporterException;
import org.alfresco.repo.importer.ImporterProgress;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;


/**
 * Importer for parsing and importing nodes given the Repository View schema.
 * 
 * @author David Caruana
 */
public class ViewImporter implements Importer
{
    // Logger
    private static final Log logger = LogFactory.getLog(ViewImporter.class);
    
    // View schema elements and attributes
    private static final String VIEW_CHILD_NAME_ATTR = "childName";    
    
    // XML Pull Parser Factory
    private XmlPullParserFactory factory;
    
    // Supporting services
    private NamespaceService namespaceService;
    private NodeService nodeService;
    private DictionaryService dictionaryService;
    
    
    /**
     * Construct
     */
    public ViewImporter()
    {
        try
        {
            // Construct Xml Pull Parser Factory
            factory = XmlPullParserFactory.newInstance(System.getProperty(XmlPullParserFactory.PROPERTY_NAME), this.getClass());
            factory.setNamespaceAware(true);
        }
        catch (XmlPullParserException e)
        {
            throw new ImporterException("Failed to initialise view importer", e);
        }
    }
    
    /**
     * @param nodeService  the node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * @param namespaceService  the namespace service
     */
    public void setNamespaceService(NamespaceService namespaceService)
    {
        this.namespaceService = namespaceService;
    }
    
    /**
     * @param dictionaryService  the dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.Importer#importNodes(java.io.InputStream, org.alfresco.service.cmr.repository.NodeRef, org.alfresco.service.namespace.QName, org.alfresco.repo.importer.ImporterProgress)
     */
    public void importNodes(InputStream inputStream, NodeRef parentRef, QName childAssocType, ImporterProgress progress)
    {
        try
        {
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(inputStream, null);
            Stack<ElementContext> contextStack = new Stack<ElementContext>();
            
            for (int eventType = xpp.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xpp.next())
            {
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                    {
                        if (xpp.getDepth() == 1)
                        {
                            processRoot(xpp, parentRef, childAssocType, progress, contextStack);
                        }
                        else
                        {
                            processStartElement(xpp, contextStack);
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:
                    {
                        processEndElement(xpp, contextStack);
                        break;
                    }
                }
            }
        }
        catch(XmlPullParserException e)
        {
            throw new ImporterException("Failed to parse view", e);
        }
        catch(IOException e)
        {
            throw new ImporterException("Failed to parse view", e);
        }
    }
    
    /**
     * Process start of xml element
     * 
     * @param xpp
     * @param contextStack
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void processStartElement(XmlPullParser xpp, Stack<ElementContext> contextStack)
        throws XmlPullParserException, IOException
    {
        // Extract qualified name
        QName defName = getName(xpp);
    
        // Process the element
        Object context = contextStack.peek();
        if (context instanceof ParentContext)
        {
            // Process type definition 
            TypeDefinition typeDef = dictionaryService.getType(defName);
            if (typeDef == null)
            {
                throw new ImporterException("Type " + defName + " has not been defined in the Repository dictionary");
            }
            processStartType(xpp, typeDef, contextStack);
            return;
        }
        else if (context instanceof NodeContext)
        {
            // Process children of node
            // Note: Process in the following order: aspects, properties and associations
            AspectDefinition aspectDef = dictionaryService.getAspect(defName);
            if (aspectDef != null)
            {
                processAspect(xpp, aspectDef, contextStack);
                return;
            }
            
            PropertyDefinition propDef = dictionaryService.getProperty(defName);
            if (propDef != null)
            {
                processProperty(xpp, propDef, contextStack);
                return;
            }
            
            AssociationDefinition assocDef = dictionaryService.getAssociation(defName);
            if (assocDef != null)
            {
                if (assocDef.isChild())
                {
                    processStartChildAssoc(xpp, (ChildAssociationDefinition)assocDef, contextStack);
                }
                else
                {
                    // TODO: process general association
                }
                return;
            }
            
            // Definition does not exist
            throw new ImporterException("Definition " + defName + " has not been defined in the Repository dictionary");
        }
    }

    /**
     * Process root element
     * 
     * @param xpp
     * @param parentRef
     * @param childAssocType
     * @param progress
     * @param contextStack
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void processRoot(XmlPullParser xpp, NodeRef parentRef, QName childAssocType, ImporterProgress progress, Stack<ElementContext> contextStack)
        throws XmlPullParserException, IOException
    {
        ParentContext parentContext = new ParentContext(dictionaryService, progress, getName(xpp), parentRef, childAssocType);
        contextStack.push(parentContext);
        
        if (logger.isDebugEnabled())
            logger.debug(indentLog("Pushed " + parentContext, contextStack.size() -1));
    }
    
    /**
     * Process start of a node definition
     * 
     * @param xpp
     * @param typeDef
     * @param contextStack
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void processStartType(XmlPullParser xpp, TypeDefinition typeDef, Stack<ElementContext> contextStack)
        throws XmlPullParserException, IOException
    {
        ParentContext parentContext = (ParentContext)contextStack.peek();
        NodeContext context = new NodeContext(typeDef.getName(), parentContext, typeDef);
        
        // Extract child name if explicitly defined
        String childName = xpp.getAttributeValue(NamespaceService.ALFRESCO_VIEW_URI, VIEW_CHILD_NAME_ATTR);
        if (childName != null && childName.length() > 0)
        {
            context.setChildName(QName.createQName(childName, namespaceService));
        }
            
        contextStack.push(context);
        
        if (logger.isDebugEnabled())
            logger.debug(indentLog("Pushed " + context, contextStack.size() -1));
    }

    /**
     * Process aspect definition
     * 
     * @param xpp
     * @param aspectDef
     * @param contextStack
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void processAspect(XmlPullParser xpp, AspectDefinition aspectDef, Stack<ElementContext> contextStack)
        throws XmlPullParserException, IOException
    {
        NodeContext context = (NodeContext)contextStack.peek();
        context.addAspect(aspectDef);
        
        int eventType = xpp.next();
        if (eventType != XmlPullParser.END_TAG)
        {
            throw new ImporterException("Aspect " + aspectDef.getName() + " definition is not valid - it cannot contain any elements");
        }
        
        if (logger.isDebugEnabled())
            logger.debug(indentLog("Processed aspect " + aspectDef.getName(), contextStack.size()));
    }

    /**
     * Process property definition
     * 
     * @param xpp
     * @param propDef
     * @param contextStack
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void processProperty(XmlPullParser xpp, PropertyDefinition propDef, Stack<ElementContext> contextStack)
        throws XmlPullParserException, IOException
    {
        NodeContext context = (NodeContext)contextStack.peek();
        
        // Extract value
        Serializable value = null;
        int eventType = xpp.next();
        if (eventType == XmlPullParser.TEXT)
        {
            String strValue = xpp.getText();
            value = (Serializable)ValueConverter.convert(propDef.getPropertyType(), strValue);
            eventType = xpp.next();
        }
        if (eventType != XmlPullParser.END_TAG)
        {
            throw new ImporterException("Property " + propDef.getName() + " definition is invalid - it cannot contain any elements");
        }
        
        context.addProperty(propDef, value);
        
        if (logger.isDebugEnabled())
            logger.debug(indentLog("Processed property " + propDef.getName(), contextStack.size()));
    }

    /**
     * Process start of child association definition
     * 
     * @param xpp
     * @param childAssocDef
     * @param contextStack
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void processStartChildAssoc(XmlPullParser xpp, ChildAssociationDefinition childAssocDef, Stack<ElementContext> contextStack)
        throws XmlPullParserException, IOException
    {
        NodeContext context = (NodeContext)contextStack.peek();
    
        // Create Node
        if (context.getNodeRef() == null)
        {
            importNode(context);
        }
        
        // Construct Child Association Context
        ParentContext parentContext = new ParentContext(childAssocDef.getName(), context, childAssocDef);
        contextStack.push(parentContext);
        
        if (logger.isDebugEnabled())
            logger.debug(indentLog("Pushed " + parentContext, contextStack.size() -1));
    }

    /**
     * Process end of xml element
     * 
     * @param xpp
     * @param contextStack
     */
    private void processEndElement(XmlPullParser xpp, Stack<ElementContext> contextStack)
    {
        ElementContext context = contextStack.peek();
        if (context.getElementName().getLocalName().equals(xpp.getName()) &&
            context.getElementName().getNamespaceURI().equals(xpp.getNamespace()))
        {
            context = contextStack.pop();
            
            if (logger.isDebugEnabled())
                logger.debug(indentLog("Popped " + context, contextStack.size()));

            if (context instanceof NodeContext)
            {
                processEndType((NodeContext)context);
            }
            else if (context instanceof ParentContext)
            {
                processEndChildAssoc((ParentContext)context);
            }
        }
    }
    
    /**
     * Process end of the type definition
     * 
     * @param context
     */
    private void processEndType(NodeContext context)
    {
        if (context.getNodeRef() == null)
        {
            importNode(context);
        }
    }

    /**
     * Process end of the child association
     * 
     * @param context
     */
    private void processEndChildAssoc(ParentContext context)
    {
    }

    /**
     * Import a Node
     * 
     * @param context
     */
    private void importNode(NodeContext context)
    {
        NodeRef parentRef = context.getParentContext().getParentRef();
        QName assocType = context.getParentContext().getAssocType();
        QName childName = context.getChildName();
        TypeDefinition nodeType = context.getTypeDefinition();
        
        if (childName == null)
        {
            throw new ImporterException("Cannot import node of type " + nodeType.getName() + " - it does not have a name");
        }
        
        // Build initial map of properties
        Map<QName, Serializable> initialProperties = new HashMap<QName, Serializable>();
        initialProperties.putAll(context.getProperties(nodeType.getName()));
        List<AspectDefinition> defaultAspects = nodeType.getDefaultAspects();
        for (AspectDefinition aspect: defaultAspects)
        {
            initialProperties.putAll(context.getProperties(aspect.getName()));
        }
        
        // Create initial node
        ChildAssociationRef assocRef = nodeService.createNode(parentRef, assocType, childName, nodeType.getName(), initialProperties);
        NodeRef nodeRef = assocRef.getChildRef();
        reportNodeCreated(context.getImporterProgress(), assocRef);
        reportPropertySet(context.getImporterProgress(), nodeRef, initialProperties);
        
        // Apply aspects
        for (QName aspect : context.getNodeAspects())
        {
            Map<QName, Serializable> aspectProperties = context.getProperties(aspect); 
            nodeService.addAspect(nodeRef, aspect, aspectProperties);
            reportAspectAdded(context.getImporterProgress(), nodeRef, aspect);
            reportPropertySet(context.getImporterProgress(), nodeRef, aspectProperties);
        }
        
        context.setNodeRef(nodeRef);
    }

    /**
     * Helper to report node created progress
     * 
     * @param progress
     * @param childAssocRef
     */
    private void reportNodeCreated(ImporterProgress progress, ChildAssociationRef childAssocRef)
    {
        if (progress != null)
        {
            progress.nodeCreated(childAssocRef.getChildRef(), childAssocRef.getParentRef(), childAssocRef.getTypeQName(), childAssocRef.getQName());
        }
    }
    
    /**
     * Helper to report aspect added progress
     *  
     * @param progress
     * @param nodeRef
     * @param aspect
     */
    private void reportAspectAdded(ImporterProgress progress, NodeRef nodeRef, QName aspect)
    {
        if (progress != null)
        {
            progress.aspectAdded(nodeRef, aspect);
        }        
    }

    /**
     * Helper to report property set progress
     * 
     * @param progress
     * @param nodeRef
     * @param properties
     */
    private void reportPropertySet(ImporterProgress progress, NodeRef nodeRef, Map<QName, Serializable> properties)
    {
        if (progress != null)
        {
            for (QName property : properties.keySet())
            {
                progress.propertySet(nodeRef, property, properties.get(property));
            }
        }
    }

    /**
     * Helper to create Qualified name from current xml element
     * 
     * @param xpp
     * @return
     */
    private QName getName(XmlPullParser xpp)
    {
        // Ensure namespace is valid
        String uri = xpp.getNamespace();
        if (namespaceService.getURIs().contains(uri) == false)
        {
            throw new ImporterException("Namespace URI " + uri + " has not been defined in the Repository dictionary");
        }
        
        // Construct name
        String name = xpp.getName();
        return QName.createQName(uri, name);
    }
    
    /**
     * Helper to indent debug output
     * 
     * @param msg
     * @param depth
     * @return
     */
    private String indentLog(String msg, int depth)
    {
        StringBuffer buf = new StringBuffer(1024);
        for (int i = 0; i < depth; i++)
        {
            buf.append(' ');
        }
        buf.append(msg);
        return buf.toString();
    }
    
}
