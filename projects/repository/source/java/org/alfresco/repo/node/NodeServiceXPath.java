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
import org.alfresco.repo.dictionary.PropertyTypeDefinition;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.search.QueryParameterDefinition;
import org.jaxen.BaseXPath;
import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.function.StringFunction;

public class NodeServiceXPath extends BaseXPath
{

    /**
     * 
     */
    private static final long serialVersionUID = 3834032441789592882L;
    private boolean followAllParentLinks;

    public NodeServiceXPath(String arg0, NodeService nodeService, NamespacePrefixResolver nspr, QueryParameterDefinition[] paramDefs, boolean followAllParentLinks) throws JaxenException
    {
        super(arg0, new DocumentNavigator(nodeService, nspr, followAllParentLinks));
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
        // TODO:Register extra functions here
        sfc.registerFunction(null, "deref", new Deref());
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

}
