/*
 * Created on 18-May-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.node;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.debug.CodeMonkey;
import org.jaxen.BaseXPath;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.function.BooleanFunction;
import org.jaxen.function.StringFunction;

/**
 * @author Andy Hind
 */
public class NodeServiceXPath extends BaseXPath
{
    private static final long serialVersionUID = 3834032441789592882L;
    
    private boolean followAllParentLinks;

    public NodeServiceXPath(
            String xpath,
            DictionaryService dictionaryService,
            NodeService nodeService,
            NamespacePrefixResolver nspr,
            QueryParameterDefinition[] paramDefs,
            boolean followAllParentLinks) throws JaxenException
    {
        super(xpath, new DocumentNavigator(dictionaryService, nodeService, nspr, followAllParentLinks));
        
        // Add support for parameters
        if (paramDefs != null)
        {
            SimpleVariableContext svc = (SimpleVariableContext) this.getVariableContext();
            for (int i = 0; i < paramDefs.length; i++)
            {
                if (!paramDefs[i].hasDefaultValue())
                {
                    throw new AlfrescoRuntimeException("Parameter must have default value");
                }
                Object value = null;
                if (paramDefs[i].getPropertyTypeDefinition().getName().equals(PropertyTypeDefinition.BOOLEAN))
                {
                    value = Boolean.valueOf(paramDefs[i].getDefault());
                }
                else if (paramDefs[i].getPropertyTypeDefinition().getName().equals(PropertyTypeDefinition.DOUBLE))
                {
                    value = Double.valueOf(paramDefs[i].getDefault());
                }
                else if (paramDefs[i].getPropertyTypeDefinition().getName().equals(PropertyTypeDefinition.FLOAT))
                {
                    value = Float.valueOf(paramDefs[i].getDefault());
                }
                else if (paramDefs[i].getPropertyTypeDefinition().getName().equals(PropertyTypeDefinition.INT))
                {
                    value = Integer.valueOf(paramDefs[i].getDefault());
                }
                else if (paramDefs[i].getPropertyTypeDefinition().getName().equals(PropertyTypeDefinition.LONG))
                {
                    value = Long.valueOf(paramDefs[i].getDefault());
                }
                else
                {
                    value = paramDefs[i].getDefault();
                }
                svc.setVariableValue(paramDefs[i].getQName().getNamespaceURI(), paramDefs[i].getQName().getLocalName(), value);
            }
        }
        SimpleFunctionContext sfc = (SimpleFunctionContext) this.getFunctionContext();
        // Register extra functions here - both null and empty namespace
        // TODO: Issue - Investigate why both null and empty string ns are required for the functions
        sfc.registerFunction("", "subtypeOf", new SubTypeOf());
        sfc.registerFunction("", "deref", new Deref());
        sfc.registerFunction("", "like", new Like());
        sfc.registerFunction("", "contains", new Contains());
        
        sfc.registerFunction(null, "subtypeOf", new SubTypeOf());
        sfc.registerFunction(null, "deref", new Deref());
        sfc.registerFunction(null, "like", new Like());
        sfc.registerFunction(null, "contains", new Contains());
    }

    /**
     * A boolean function to determine if a node type is a subtype of another type  
     */
    static class SubTypeOf implements Function
    {
        public Object call(Context context, List args) throws FunctionCallException
        {
            if (args.size() != 1)
            {
                throw new FunctionCallException("subtypeOf() requires one argument: subtypeOf(QName typeQName)");
            }
            return evaluate(context.getNodeSet(), args.get(0), context.getNavigator());
        }

        public Object evaluate(List nodes, Object qnameObj, Navigator nav)
        {
            if(nodes.size() != 1)
            {
                return false;
            }
            // resolve the qname of the type we are checking for
            String qnameStr = StringFunction.evaluate(qnameObj, nav);
            QName typeQName = QName.createQName(qnameStr);
            // resolve the noderef
            NodeRef nodeRef = null;
            if(nav.isElement(nodes.get(0)))
            {
                nodeRef = ((ChildAssociationRef)nodes.get(0)).getChildRef();
            }
            else if(nav.isAttribute(nodes.get(0)))
            {   
                nodeRef = ((DocumentNavigator.Property)nodes.get(0)).parent;
            }
            
            DocumentNavigator dNav = (DocumentNavigator)nav;
            boolean result = dNav.isSubtypeOf(nodeRef, typeQName);
            return result;
        }
    }
    
    static class Deref implements Function
    {

        public Object call(Context context, List args) throws FunctionCallException
        {
            if (args.size() == 2)
            {
                return evaluate(args.get(0), args.get(1), context.getNavigator());
            }

            throw new FunctionCallException("deref() requires two arguments.");
        }

        public Object evaluate(Object attributeName, Object pattern, Navigator nav)
        {
            List<Object> answer = new ArrayList<Object>();
            String attributeValue = StringFunction.evaluate(attributeName, nav);
            String patternValue = StringFunction.evaluate(pattern, nav);

            // TODO:  Ignore the pattern for now
            // Should do a type pattern test
            if((attributeValue != null) && (attributeValue.length() > 0))
            {
               NodeRef nodeRef = new NodeRef(attributeValue);
               DocumentNavigator dNav = (DocumentNavigator)nav;
               answer.add(dNav.getNode(nodeRef));
               
            }
            return answer;
            
        }
    }
    
    /**
     * A boolean function to determine if a node property matches a pattern and/or the node
     * text matches the pattern.
     * <p>
     * The default is JSR170 compliant.  The optional boolean allows searching only against
     * the property value itself.
     * 
     * @author Derek Hulley
     */
    static class Like implements Function
    {
   
        public Object call(Context context, List args) throws FunctionCallException
        {
            if (args.size() < 2 || args.size() > 3)
            {
                throw new FunctionCallException(
                        "like() usage: like(@attr, 'pattern' [, includeFTS]) \n" +
                        " - includeFTS can be 'true' or 'false'");
            }
            // default includeFTS to true
            return evaluate(
                    context.getNodeSet(),
                    args.get(0),
                    args.get(1),
                    args.size() == 2 ? Boolean.toString(true) : args.get(2), 
                    context.getNavigator());
        }

        public Object evaluate(List nodes, Object obj, Object patternObj, Object includeFtsObj, Navigator nav)
        {
            Object attribute = null;
            if(obj instanceof List)
            {
                List list = (List) obj;
                if (list.isEmpty())
                {
                   return false;
                }
                // do not recurse: only first list should unwrap
                attribute = list.get(0);
            }
            if((attribute == null) || !nav.isAttribute(attribute))
            {
                return false;
            }
            if(nodes.size() != 1)
            {
                return false;
            }
            if(!nav.isElement(nodes.get(0)))
            {
                return false;
            }
            ChildAssociationRef car = (ChildAssociationRef)nodes.get(0);
            String pattern = StringFunction.evaluate(patternObj, nav);
            boolean includeFts = BooleanFunction.evaluate(includeFtsObj, nav);
            QName qname = QName.createQName(nav.getAttributeNamespaceUri(attribute), nav.getAttributeName(attribute));
            
            DocumentNavigator dNav = (DocumentNavigator)nav;
            // JSR 170 inclues full text matches
            return dNav.like(car.getChildRef(), qname, pattern, includeFts); 
            
        }
    }
    
    static class Contains implements Function
    {
   
        public Object call(Context context, List args) throws FunctionCallException
        {
            if (args.size() == 1)
            {
                return evaluate(context.getNodeSet(), args.get(0), context.getNavigator());
            }

            throw new FunctionCallException("contains() requires one argument.");
        }

        public Object evaluate(List nodes, Object pattern, Navigator nav)
        {
            if(nodes.size() != 1)
            {
                return false;
            }
            QName qname = null;
            NodeRef nodeRef = null;
            if(nav.isElement(nodes.get(0)))
            {
                qname = null; // should use all attributes and full text index
                nodeRef = ((ChildAssociationRef)nodes.get(0)).getChildRef();
            }
            else if(nav.isAttribute(nodes.get(0)))
            {   
                qname = QName.createQName(nav.getAttributeNamespaceUri(nodes.get(0)), nav.getAttributeName(nodes.get(0)));
                nodeRef = ((DocumentNavigator.Property)nodes.get(0)).parent;
            }
            
            String patternValue = StringFunction.evaluate(pattern, nav);
            DocumentNavigator dNav = (DocumentNavigator)nav;
            
            return dNav.contains(nodeRef, qname, patternValue); 
            
        }
    }
}
