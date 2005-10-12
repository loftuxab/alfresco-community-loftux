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
package org.alfresco.repo.importer.view;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

import org.alfresco.repo.importer.Importer;
import org.alfresco.repo.importer.Parser;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.rule.RuleService;
import org.alfresco.service.cmr.view.ImporterException;
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
public class ViewParser implements Parser
{
    // Logger
    private static final Log logger = LogFactory.getLog(ViewParser.class);
    
    // View schema elements and attributes
    private static final String VIEW_CHILD_NAME_ATTR = "childName";    
    private static final String VIEW_DATATYPE_ATTR = "datatype";
    private static final QName VIEW_VALUE_QNAME = QName.createQName(NamespaceService.REPOSITORY_VIEW_1_0_URI, "value");
    private static final QName VIEW_ASPECTS = QName.createQName(NamespaceService.REPOSITORY_VIEW_1_0_URI, "aspects");
    private static final QName VIEW_PROPERTIES = QName.createQName(NamespaceService.REPOSITORY_VIEW_1_0_URI, "properties");
    private static final QName VIEW_ASSOCIATIONS = QName.createQName(NamespaceService.REPOSITORY_VIEW_1_0_URI, "associations");
    
    // XML Pull Parser Factory
    private XmlPullParserFactory factory;
    
    // Supporting services
    private NamespaceService namespaceService;
    private DictionaryService dictionaryService;
    
    
    /**
     * Construct
     */
    public ViewParser()
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
    
    public void setRuleService(RuleService ruleService)
    {
//        this.ruleService = ruleService;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.importer.Parser#parse(java.io.Reader, org.alfresco.repo.importer.Importer)
     */
    public void parse(Reader viewReader, Importer importer)
    {
        try
        {
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(viewReader);
            Stack<ElementContext> contextStack = new Stack<ElementContext>();
            
            try
            {
                for (int eventType = xpp.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xpp.next())
                {
                    switch (eventType)
                    {
                        case XmlPullParser.START_TAG:
                        {
                            if (xpp.getDepth() == 1)
                            {
                                processRoot(xpp, importer, contextStack);
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
            catch(Exception e)
            {
                throw new ImporterException("Failed to parse view at line " + xpp.getLineNumber() + "; column " + xpp.getColumnNumber(), e);
            }
        }
        catch(XmlPullParserException e)
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

        // Handle special view directives
        if (defName.equals(VIEW_ASPECTS) || defName.equals(VIEW_PROPERTIES) || defName.equals(VIEW_ASSOCIATIONS))
        {
            if (context instanceof NodeItemContext)
            {
                throw new ImporterException("Cannot nest element " + defName + " within " + ((NodeItemContext)context).getElementName());
            }
            if (!(context instanceof NodeContext))
            {
                throw new ImporterException("Element " + defName + " can only be declared within a node");
            }
            NodeContext nodeContext = (NodeContext)context;
            contextStack.push(new NodeItemContext(defName, nodeContext));
        }
        else
        {
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
                Object def = ((NodeContext)context).determineDefinition(defName);
                if (def == null)
                {
                    throw new ImporterException("Definition " + defName + " is not valid; cannot find in Repository dictionary");
                }
                
                if (def instanceof AspectDefinition)
                {
                    processAspect(xpp, (AspectDefinition)def, contextStack);
                    return;
                }
                else if (def instanceof PropertyDefinition)
                {
                    processProperty(xpp, (PropertyDefinition)def, contextStack);
                    return;
                }
                else if (def instanceof ChildAssociationDefinition)
                {
                    processStartChildAssoc(xpp, (ChildAssociationDefinition)def, contextStack);
                    return;
                }
                else
                {
                    // TODO: general association
                }
            }
            else if (context instanceof NodeItemContext)
            {
                NodeItemContext nodeItemContext = (NodeItemContext)context;
                NodeContext nodeContext = nodeItemContext.getNodeContext();
                QName itemName = nodeItemContext.getElementName();
                if (itemName.equals(VIEW_ASPECTS))
                {
                    AspectDefinition def = nodeContext.determineAspect(defName);
                    processAspect(xpp, def, contextStack);
                }
                else if (itemName.equals(VIEW_PROPERTIES))
                {
                    PropertyDefinition def = nodeContext.determineProperty(defName);
                    processProperty(xpp, def, contextStack);
                }
                else if (itemName.equals(VIEW_ASSOCIATIONS))
                {
                    // TODO: Handle general associations...  
                    
                    ChildAssociationDefinition def = (ChildAssociationDefinition)nodeContext.determineAssociation(defName);
                    processStartChildAssoc(xpp, def, contextStack);
                }
            }
        }
    }

    /**
     * Process Root
     * 
     * @param xpp
     * @param parentRef
     * @param childAssocType
     * @param configuration
     * @param progress
     * @param contextStack
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void processRoot(XmlPullParser xpp, Importer importer, Stack<ElementContext> contextStack)
        throws XmlPullParserException, IOException
    {
        ParentContext parentContext = new ParentContext(getName(xpp), dictionaryService, importer);
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
        String childName = xpp.getAttributeValue(NamespaceService.REPOSITORY_VIEW_1_0_URI, VIEW_CHILD_NAME_ATTR);
        if (childName != null && childName.length() > 0)
        {
            context.setChildName(childName);
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
        NodeContext context = peekNodeContext(contextStack);
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
        NodeContext context = peekNodeContext(contextStack);
        
        int eventType = xpp.next();
        if (eventType == XmlPullParser.TEXT)
        {
            // Extract single value
            String value = xpp.getText();
            eventType = xpp.next();
            if (eventType == XmlPullParser.END_TAG)
            {
                context.addProperty(propDef, value);
            }
        }

        // Extract multi value
        while (eventType == XmlPullParser.START_TAG)
        {
            QName name = getName(xpp);
            if (!name.equals(VIEW_VALUE_QNAME))
            {
                throw new ImporterException("Invalid view structure - expected element " + VIEW_VALUE_QNAME + " for property " + propDef.getName());
            }
            QName datatype = QName.createQName(xpp.getAttributeValue(NamespaceService.REPOSITORY_VIEW_1_0_URI, VIEW_DATATYPE_ATTR), namespaceService);
            eventType = xpp.next();
            if (eventType == XmlPullParser.TEXT)
            {
                String value = xpp.getText();
                context.addProperty(propDef, value);
                if (datatype != null)
                {
                    context.addDatatype(propDef.getName(), dictionaryService.getDataType(datatype));
                }
                eventType = xpp.next();
            }
            if (eventType != XmlPullParser.END_TAG)
            {
                throw new ImporterException("Value for property " + propDef.getName() + " has not been defined correctly - missing end tag");
            }
            eventType = xpp.next();
            if (eventType == XmlPullParser.TEXT)
            {
                eventType = xpp.next();
            }
        }

        // End of Property
        if (eventType != XmlPullParser.END_TAG)
        {
            throw new ImporterException("Property " + propDef.getName() + " definition is invalid");
        }
        
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
        NodeContext context = peekNodeContext(contextStack);
    
        if (context.getNodeRef() == null)
        {
            // Create Node
            NodeRef nodeRef = context.getImporter().importNode(context);
            context.setNodeRef(nodeRef);
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
        NodeRef nodeRef = context.getNodeRef();
        if (nodeRef == null)
        {
            nodeRef = context.getImporter().importNode(context);
            context.setNodeRef(nodeRef);
        }
        context.getImporter().childrenImported(nodeRef);
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
     * Get parent Node Context
     * 
     * @param contextStack  context stack
     * @return  node context
     */
    private NodeContext peekNodeContext(Stack<ElementContext> contextStack)
    {
        ElementContext context = contextStack.peek();
        if (context instanceof NodeContext)
        {
            return (NodeContext)context;
        }
        else if (context instanceof NodeItemContext)
        {
            return ((NodeItemContext)context).getNodeContext();
        }
        throw new ImporterException("Internal error: Failed to retrieve node context");
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
