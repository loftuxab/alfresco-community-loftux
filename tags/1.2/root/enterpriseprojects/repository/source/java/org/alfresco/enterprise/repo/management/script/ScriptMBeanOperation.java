/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectName;

import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.scripts.ScriptException;
import org.mozilla.javascript.Scriptable;

/**
 * This class provides a JavaScript-friendly API for exposing the metadat on MBean operation objects.
 * This is basically the method signature.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class ScriptMBeanOperation implements Scopeable
{
    /**
     * This class provides a JavaScript friendly wrapper for basic JMX operation parameter info.
     */
    public class ScriptMBeanOperationParameter implements Scopeable
    {
        private final MBeanParameterInfo paramInfo;
        @SuppressWarnings("unused")
        private Scriptable scope;
        
        public ScriptMBeanOperationParameter(MBeanParameterInfo paramInfo, Scriptable scope)
        {
            this.paramInfo = paramInfo;
            this.scope = scope;
        }
        
        @Override public void setScope(Scriptable scope) { this.scope = scope; }
        
        /** Returns the name of this parameter. */
        public String getName() { return paramInfo.getName(); }
        
        /** Returns the type of this parameter. */
        public String getType() { return paramInfo.getType(); }
        
        /** Returns the description for this parameter. */
        public String getDescription() { return paramInfo.getDescription(); }
        
        @Override public String toString()
        {
            StringBuilder msg = new StringBuilder();
            msg.append(getName())
               .append(":")
               .append(getType());
            return msg.toString();
        }
    }
    
    private final ObjectName                  containingBean;
    private final MBeanOperationInfo          operationInfo;
    
    // JavaScript-related stuff
    private Scriptable            scope;
    
    public ScriptMBeanOperation(ObjectName containingBean, MBeanOperationInfo operationInfo, Scriptable scope)
    {
        if (operationInfo == null)         { throw new ScriptException("Null operationInfo"); }
        
        this.containingBean = containingBean;
        this.operationInfo = operationInfo;

        this.scope = scope;
    }
    
    /** Gets the name of this operation. */
    public String getName()        { return operationInfo.getName(); }
    
    /** Gets the description of this operation. */
    public String getDescription() { return operationInfo.getDescription(); }
    
    /** Gets the Java return type of this operation.
     * @return "java.lang.String" or "void"
     */
    public String getReturnType()  { return operationInfo.getReturnType(); }
    
    /**
     * Gets parameter data for any parameters on this operation.
     */
    public ScriptMBeanOperationParameter[] getParameters()
    {
        List<ScriptMBeanOperationParameter> result = new ArrayList<ScriptMBeanOperationParameter>();
        for (MBeanParameterInfo paramInfo : operationInfo.getSignature())
        {
            result.add(new ScriptMBeanOperationParameter(paramInfo, scope));
        }
        return result.toArray(new ScriptMBeanOperationParameter[0]);
    }
    
    @Override public void setScope(Scriptable scope) { this.scope = scope; }
    
    @Override public int hashCode() { return containingBean.hashCode() + 7 * operationInfo.hashCode(); }
    
    @Override public boolean equals(Object otherObj)
    {
        if (this == otherObj) return true;
        if (otherObj == null) return false;
        if (getClass() != otherObj.getClass()) return false;
        
        ScriptMBeanOperation otherOperation = (ScriptMBeanOperation) otherObj;
        return operationInfo.equals(otherOperation.operationInfo) && containingBean.equals(otherOperation.containingBean);
    }
    
    @Override public String toString()
    {
        StringBuilder msg = new StringBuilder();
        
        msg.append(ScriptMBeanOperation.class.getSimpleName())
           .append("[")
           .append(containingBean).append(".")
           .append(operationInfo.getName())
           .append("(");
        for (int i = 0; i < getParameters().length; i++)
        {
            msg.append(getParameters()[i].getType());
            if (i < getParameters().length - 1)
            {
                msg.append(", ");
            }
        }
        msg.append(")")
           .append("]");
           
        return msg.toString();
    }
}