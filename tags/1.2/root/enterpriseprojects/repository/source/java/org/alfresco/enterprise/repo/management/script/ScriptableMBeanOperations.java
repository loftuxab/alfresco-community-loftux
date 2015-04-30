/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import static org.mozilla.javascript.Context.javaToJS;
import static org.mozilla.javascript.Context.jsToJava;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.alfresco.repo.jscript.ScriptableHashMap;
import org.alfresco.scripts.ScriptException;
import org.alfresco.util.collections.CollectionUtils;
import org.alfresco.util.collections.Function;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.springframework.util.ClassUtils;

/**
 * This class provides a friendly script API for MBean operations. JMX operations are provided as JavaScript functions with operation
 * metadata (primarily method signatures) exposed as a JavaScript {@link #METADATA_PROPERTY property}.
 * <p/>
 * JMX operations are exposed in the JavaScript API in two forms. All operations are exposed in a 'type-aware' form:
 * <pre>
 * // The trailing underscore is intentional here.
 * bean.operations.doSomething_(['java.lang.String', 'int'], 'hello', 42);
 * </pre>
 * where the JavaScript code must provide an array of Strings representing the Java types of the JMX operation parameters. This is
 * to support the invocation of overloaded JMX operations - something which does not map easily to JavaScript. Autoboxing and
 * unboxing of the provided type Strings is not supported. You must specify exactly the right Java type. Boxing & unboxing of the
 * parameter values happens in the normal Java way.
 * <p/>
 * A second 'simple' form is available only for JMX operations which are not overloaded with different method signatures varying
 * only by Java type.
 * <pre>
 * bean.operations.doSomething('hello', 42);
 * </pre>
 * For example, an MBean defining operations doSomething(String) and doSomething(String, String) would be callable in the simple or type-aware
 * form. An MBean defining operations doSomething(String) and doSomething(int) would not be callable in the simple form, only the
 * type-aware form.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class ScriptableMBeanOperations extends ScriptableObject
{
    private static final long serialVersionUID = 1L;
    
    private static Log log = LogFactory.getLog(ScriptableMBeanOperations.class);
    
    /**
     * The JavaScript property name which holds the JMX operation metadata.
     */
    private static final String METADATA_PROPERTY = "metadata";
    
    private final ObjectName objectName;
    private final MBeanInfo mbeanInfo;
    
    private MBeanServerConnection mbeanServerConnection;
    private JmxValueConversionChain conversionChain;
    
    /**
     * This map stores metadata objects for each of the JMX operations defined for this MBean.
     */
    private final ScriptableHashMap<String, ScriptMBeanOperation> operationsMetadata = new ScriptableHashMap<String, ScriptMBeanOperation>();
    
    public void setMBeanServer(MBeanServerConnection mbeanServerConnection) { this.mbeanServerConnection = mbeanServerConnection; }
    
    public void setConversionChain(JmxValueConversionChain conversionChain) { this.conversionChain = conversionChain; }
    
    /**
     * This factory method constructs a new instance of this class.
     * 
     * @param objectName the MBean Object Name.
     * @param mbeanInfo  the MBean info.
     * @return a new instance.
     */
    public static ScriptableMBeanOperations create(ObjectName objectName, MBeanInfo mbeanInfo, Scriptable scope)
    {
        return new ScriptableMBeanOperations(objectName, mbeanInfo, scope);
    }
    
    /**
     * Private constructor to prevent instantiation other than by {@link #create(ObjectName, MBeanInfo) the factory method}.
     */
    private ScriptableMBeanOperations(ObjectName objectName, MBeanInfo mbeanInfo, Scriptable scope)
    {
        this.objectName = objectName;
        this.mbeanInfo  = mbeanInfo;
        this.setParentScope(scope);
    }
    
    /**
     * This method goes through the JMX operations defined for this MBean and makes them accessible from JavaScript.
     */
    @SuppressWarnings("unchecked")
    public void initJmxOperations()
    {
        // First, go through all the JMX operations for this MBean.
        final MBeanOperationInfo[] allOps_ = mbeanInfo.getOperations();
        // ...and convert them to a friendlier type
        final List<JmxOp> allOps = CollectionUtils.transform(Arrays.asList(allOps_), new Function<MBeanOperationInfo, JmxOp>()
        {
            @Override public JmxOp apply(MBeanOperationInfo value) { return new JmxOp(value); }
        });
        
        if (log.isTraceEnabled())
        {
            log.trace("Initialising " + allOps.size() + " JMX operations for bean " + this.objectName);
        }
        
        // We need to link all these JMX operations (Java methods) to JavaScript functions.
        // However, because JavaScripts sees overloaded functions as being the same function (with different arguments),
        // we need to group any overloaded JMX operations together for special handling.
        
        // We'll use a MultiMap to build up the groups first.
        // MultiValueMap<String, JmxOp>
        final MultiValueMap allOpsByName_ = new MultiValueMap();
        for (JmxOp op : allOps)
        {
            allOpsByName_.put(op.getName(), op);
        }
        // ...and convert the MultiMap into a friendlier data form.
        final Map<String, JmxOpGroup> allOpsByName = new HashMap<String, JmxOpGroup>();
        for (Object o : allOpsByName_.entrySet())
        {
            Map.Entry<String, List<JmxOp>> entry = (Entry<String, List<JmxOp>>) o;
            JmxOpGroup jmxOpsWithSameName = new JmxOpGroup(entry.getKey(), entry.getValue());
            allOpsByName.put(entry.getKey(), jmxOpsWithSameName);
        }
        
        
        // Now we should have a Map of all JMX operations keyed by their name.
        // They are grouped together by their operation name, so overloaded operations/methods will be in a single group together.
        
        
        // Now before we start mapping the JMX operations to JavaScript functions, we must consider another
        // JavaScript-Java impedance mismatch: method/function overloading and strong/weak typing.
        //
        // Consider Java overloading. Although the JMX spec recommends against it, we could have JMX operations as follows:
        //    doSomething()
        //    doSomething(String)
        //    doSomething(String, String)
        //    doSomething(String, float)
        // Because these would all be seen as one JavaScript function with differing parameters, how would Rhino or Alfresco route a
        // JS function call to the correct JMX operation (Java method)?
        //
        // In some cases it is unambiguous:
        // JavaScript: doSomething(); is clearly a call to the first JMX operation.
        // JavaScript: doSomething('hello'); is clearly a call the second.
        //
        // But for some cases it is ambiguous:
        // JavaScript: doSomething('hello', 42); is not easily routeable to a single JMX operation.
        // Yes, this looks like a [String, Number] but if the types were not Strings and Numbers, but instead
        // more complex types, perhaps provided by a third party, how could we know where to dispatch the call?
        // The answer is that in cases where multiple JMX operations are overloaded and have the same parameter *count*,
        // we cannot know.
        // In these 'ambiguous' cases, we must ask the JavaScript client code to provide type information.
        
        
        // OK, So we've got a Collection of all the MBeanOperations that we need to expose in the JS API.
        // And we've got methods to let us split these groups up into ambiguous and unambiguous operations.
        
        // Now we need to link each of these JMX operations to a JavaScript function so that they can be called
        // directly from JavaScript. In fact we will link each to either 1 or 2 functions depending on its 'ambiguity'.
        
        
        // Go through every JMX operation on this bean.
        for (Map.Entry<String, JmxOpGroup> entry : allOpsByName.entrySet())
        {
            final String opName = entry.getKey();
            
            // All JMX operations get the type-aware JavaScript function & metadata
            // A single JavaScript function for all operations.
            registerTypeAwareJsFunction(opName, entry.getValue().getAllOps());
            
            // Individual metadata keys for each JMX operation.
            for (JmxOp op : entry.getValue().getAllOps())
            {
                String metadataEntryKey = op.getSignatureString();
                operationsMetadata.put(metadataEntryKey, new ScriptMBeanOperation(objectName, op.getMBeanOperationInfo(), getParentScope()));
            }
            // We must bind the 'metadata' data as a property of this JavaScript object.
            final List<ScriptMBeanOperation> mbeanOpList = new ArrayList<ScriptMBeanOperation>();
            mbeanOpList.addAll(this.operationsMetadata.values());
            
            // We'll sort the collection of operations alphabetically by name as it makes testing easier.
            Collections.sort(mbeanOpList, new Comparator<ScriptMBeanOperation>()
            {
                @Override public int compare(ScriptMBeanOperation op1, ScriptMBeanOperation op2) { return op1.toString().compareTo(op2.toString()); }
            });
            
            final ScriptMBeanOperation[] mbeanOpArray = mbeanOpList.toArray(new ScriptMBeanOperation[0]);
            Object jsArray = javaToJS(mbeanOpArray, getParentScope());
            put(METADATA_PROPERTY, this, jsArray);
            
            
            
            // Only unambiguous JMX operations get the 'simple' JavaScript function.
            registerSimpleJsFunction(opName, entry.getValue().getUnambiguousOps(), entry.getValue().getAmbiguousOps());
            
            // And they do not get any metadata recorded.
        }
    }
    
    private void registerTypeAwareJsFunction(final String opName, final List<JmxOp> ops)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Registering type-aware JS function for JMX operations: " + ops);
        }
        
        final String registeredOpName = opName + "_";
        
        FunctionObject typeAwareFunction;
        try
        {
            final Method method = this.getClass().getMethod("toString", new Class<?>[0]);
            
            typeAwareFunction = new TypedJmxFunctionObject(registeredOpName, method, this)
            {
                private static final long serialVersionUID = 1L;
                
                @Override public Object call(Context context, Scriptable scope, Scriptable jsThisObject, Object[] jsArgs)
                {
                    Object result;
                    
                    // A type-aware JavaScript function will have been given a String[] of Java types for the parameters.
                    if (jsArgs[0] == null || !(jsArgs[0] instanceof NativeArray))
                    {
                        throw new WrappedException(new IllegalArgumentException("First parameter to '" + opName + "' function must be an array of Java type names."));
                    }
                    NativeArray nativeArray = (NativeArray) jsArgs[0];
                    String[] typeStrings = new String[(int) nativeArray.getLength()];
                    for (Object o : nativeArray.getIds()) {
                        int index = (Integer) o;
                        typeStrings[index] = (String) nativeArray.get(index, null);
                    }
                    
                    
                    final Object[] usableJsArgs = getJsArgs(jsArgs);
                    
                    // We must convert the JMX operation parameter values from the JavaScript values to the Java values expected by JMX.
                    Object[] convertedArgs = new Object[usableJsArgs.length];
                    try
                    {
                        for (int i = 0; i < typeStrings.length; i++)
                        {
                            Class<?> paramClass = ClassUtils.forName(typeStrings[i], null);
                            convertedArgs[i] = jsToJava(usableJsArgs[i], paramClass);
                        }
                        
                        result = mbeanServerConnection.invoke(objectName, opName, convertedArgs, typeStrings);
                        if (conversionChain.canConvertToJavaScript(result))
                        {
                            result = conversionChain.convertToJavaScript(result, scope);
                        }
                        return result;
                    }
                    catch (Throwable t)
                    {
                        // We cannot let any exceptions other than WrappedExceptions escape from this call() function.
                        // This is really down to the difference between Java's and JavaScript's exception models.
                        // Java exceptions thrown during JavaScript function execution are not automatically handled by Rhino
                        // and we must do the wrapping ourselves.
                        throw new WrappedException(t);
                    }
                    // Could skip some operations and add them as so: result = getUndefinedValue();
                }
            };
        }
        catch (NoSuchMethodException nsmx)
        {
            throw new ScriptException("Could not register function for " + opName, nsmx);
        }
        
        put(registeredOpName, this, typeAwareFunction);
    }
    
    private void registerSimpleJsFunction(final String opName, final List<JmxOp> ops, final List<JmxOp> ambiguousOps)
    {
        if (log.isTraceEnabled())
        {
            log.trace("Registering simple JS function for JMX operations: " + ops);
        }
        
        if (ops.isEmpty()) { return; }
        
        FunctionObject simpleFunction;
        
        try
        {
            final Method method = this.getClass().getMethod("toString", new Class<?>[0]);
            
            simpleFunction = new SimpleJmxFunctionObject(opName, method, this)
            {
                private static final long serialVersionUID = 1L;
                
                @Override public Object call(Context context, Scriptable scope, Scriptable jsThisObject, Object[] jsArgs)
                {
                    Object result;
                    
                    final Object[] usableJsArgs = getJsArgs(jsArgs);
                    
                    // This function implementation is only intended to support unambiguous operations.
                    // But of course, a developer might try to call an ambiguous JMX operation using the 'simple' approach.
                    // Whilst we can't dispatch that operation call, we can provide a helpful error message.
                    // We check for ambiguity based on the number of parameters provided.
                    boolean isAmbiguous = false;
                    for (JmxOp ambiguousOp : ambiguousOps)
                    {
                        if (ambiguousOp.getParameterCount() == usableJsArgs.length)
                        {
                            isAmbiguous = true;
                            break;
                        }
                    }
                    if (isAmbiguous)
                    {
                        throw new WrappedException(new IllegalArgumentException("Cannot invoke operation '" + opName
                                                              + "'. Please use type-aware JavaScript API for this operation." +
                                                              " (Add trailing underscore to function name and include types.)"));
                    }
                    
                    
                    // OK, so it is an unambiguous operation call. We should be able to dispatch it.
                    Object[] convertedArgs = new Object[usableJsArgs.length];
                    try
                    {
                        // We must convert the JMX operation parameter values from the JavaScript values to the Java values expected by JMX.
                        // So select the JMX operation that has the same number of parameters as we have received from JavaScript.
                        JmxOp jmxOp = null;
                        for (JmxOp potentialMatch : ops)
                        {
                            if (potentialMatch.getParameterCount() == convertedArgs.length)
                            {
                                jmxOp = potentialMatch;
                            }
                        }
                        
                        if (jmxOp == null)
                        {
                            throw new WrappedException(new NullPointerException("jmxOp was unexpectedly null."));
                        }
                        
                        final String[] paramTypes = jmxOp.getParameterTypes();
                        
                        for (int i = 0; i < paramTypes.length; i++)
                        {
                            Class<?> paramClass = ClassUtils.forName(paramTypes[i], null);
                            convertedArgs[i] = jsToJava(usableJsArgs[i], paramClass);
                        }
                          
                        result = mbeanServerConnection.invoke(objectName, opName, convertedArgs, paramTypes);
                        if (conversionChain.canConvertToJavaScript(result))
                        {
                            result = conversionChain.convertToJavaScript(result, scope);
                        }
                        return result;
                      }
                      catch (Throwable t)
                      {
                          // We cannot let any exceptions other than WrappedExceptions escape from this call() function.
                          // This is really down to the difference between Java's and JavaScript's exception models.
                          // Java exceptions thrown during JavaScript function execution are not automatically handled by Rhino
                          // and we must do the wrapping ourselves.
                          throw new WrappedException(t);
                      }
                      // Could skip some operations and add them as so: result = getUndefinedValue();
                  }
              };
          }
          catch (NoSuchMethodException nsmx)
          {
              throw new ScriptException("Could not register function for " + opName, nsmx);
          }
          
          put(opName, this, simpleFunction);
    }
    
    private abstract class SimpleJmxFunctionObject extends FunctionObject
    {
        private static final long serialVersionUID = 1L;
        
        public SimpleJmxFunctionObject(String name, Member methodOrConstructor, Scriptable scope)
        {
            super(name, methodOrConstructor, scope);
        }
        
        protected Object[] getJsArgs(Object[] jsArgs) { return jsArgs; }
    }
    
    private abstract class TypedJmxFunctionObject extends SimpleJmxFunctionObject
    {
        private static final long serialVersionUID = 1L;
        
        public TypedJmxFunctionObject(String name, Member methodOrConstructor, Scriptable scope)
        {
            super(name, methodOrConstructor, scope);
        }
        
        @Override protected Object[] getJsArgs(Object[] jsArgs)
        {
            if (jsArgs == null || jsArgs.length == 0)
            {
                throw new IllegalArgumentException("Illegal jsArgs length: " + (jsArgs == null ? null : jsArgs.length));
            }
            
            Object[] result = new Object[jsArgs.length - 1];
            System.arraycopy(jsArgs, 1, result, 0, jsArgs.length - 1);
            return result;
        }
    }
    
    @Override public String getClassName() { return ScriptableMBeanOperations.class.getSimpleName(); }
}

/**
 * A convenience class for dealing with MBeanOperationInfo instances.
 */
class JmxOp
{
    private final MBeanOperationInfo opInfo;
    
    public JmxOp(MBeanOperationInfo opInfo) { this.opInfo = opInfo; }
    
    public String getName() { return this.opInfo.getName(); }
    
    public int getParameterCount() { return this.opInfo.getSignature().length; }
    
    public String[] getParameterTypes()
    {
        List<String> result = new ArrayList<String>(getParameterCount());
        for (MBeanParameterInfo paramInfo : this.opInfo.getSignature())
        {
            result.add(paramInfo.getType());
        }
        return result.toArray(new String[0]);
    }
    
    public MBeanOperationInfo getMBeanOperationInfo() { return this.opInfo; }
    
    public String getSignatureString() { return this.toString(); }
    
    @Override public boolean equals(Object otherObj) { return otherObj instanceof JmxOp && ((JmxOp)otherObj).opInfo.equals(this.opInfo); }
    @Override public int     hashCode()              { return this.opInfo.hashCode(); }
    
    @Override public String toString()
    {
        StringBuilder msg = new StringBuilder();
        msg.append(this.opInfo.getName())
           .append("(");
        for (int i = 0; i < this.opInfo.getSignature().length; i++)
        {
            msg.append(this.opInfo.getSignature()[i].getType());
            if (i < this.opInfo.getSignature().length - 1)
            {
                msg.append(", ");
            }
        }
        msg.append("): ")
           .append(this.opInfo.getReturnType());
        return msg.toString();
    }
}

/**
 * A convenience class for dealing with groups of MBeanOperationInfo object.
 * The group will always have the same operation name and be on the same MBean.
 * In other words, they are a group of overloaded Java methods.
 */
class JmxOpGroup
{
    final List<JmxOp> ops;
    
    public JmxOpGroup(String opName, List<JmxOp> ops)
    {
        // Validate the data
        if (ops == null || ops.isEmpty()) { throw new IllegalArgumentException("Illegal ops for JmxOpGroup: " + ops); }
        
        final String name = ops.get(0).getName();
        for (JmxOp op : ops) { if ( !op.getName().equals(name)) throw new IllegalArgumentException("Mismatched name: " + op.getName()); }
        
        this.ops = ops;
    }
    
    @SuppressWarnings("unchecked")
    public List<JmxOp> getAmbiguousOps()
    {
        final boolean operationsAreOverloaded = ops.size() > 1;
        
        if ( !operationsAreOverloaded)
        {
            return Collections.emptyList();
        }
        else
        {
            // To identify 'ambiguous' operations we need simply identify which operations have the same number of parameters
            // as any other operation. Those operations are overloaded by parameter type, which is ambiguous.
            //
            // MultiValueMap<Integer, List<JmxOp>>
            MultiValueMap operationsKeyedByParameterCount = new MultiValueMap();
            for (JmxOp op : this.ops)
            {
                operationsKeyedByParameterCount.put(op.getParameterCount(), op);
            }
            
            List<JmxOp> result = new ArrayList<JmxOp>();
            
            for (Object entryObj : operationsKeyedByParameterCount.entrySet())
            {
                Map.Entry<Integer, List<JmxOp>> entry = (Entry<Integer, List<JmxOp>>) entryObj;
                if (entry.getValue().size() > 1)
                {
                    result.add(entry.getValue().get(0));
                }
            }
            
            return result;
        }
    }
    
    public List<JmxOp> getUnambiguousOps()
    {
        List<JmxOp> result = new ArrayList<JmxOp>();
        result.addAll(ops);
        
        org.apache.commons.collections.CollectionUtils.removeAll(result, getAmbiguousOps());
        
        return result;
    }
    
    public List<JmxOp> getAllOps() { return Collections.unmodifiableList(this.ops); }
}