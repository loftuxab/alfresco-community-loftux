/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */

package org.alfresco.enterprise.repo.management.script;

import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.mozilla.javascript.Scriptable;

/**
 * This class is the spring bean which backs the "jmx" root object within the {@link ScriptService}.
 * It doesn't actually do any of the MBean access itself, instead delegating all calls to a second spring bean
 * backed by {@link JmxScriptImpl}. This is so that we can inject role-based security on the backing {@link JmxScriptImpl} bean.
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public class JmxScriptProcessorExtension extends BaseScopableProcessorExtension implements JmxScript
{
    private JmxScript jmxScript;
    
    /**
     * Sets the {@link JmxScript} object to which this class will delegate.
     */
    public void setJmxScript(JmxScript jmxScript)
    {
        this.jmxScript = jmxScript;
    }
    
    @Override public ScriptMBean[] queryMBeans(String objectName)
    {
        return jmxScript.queryMBeans(objectName);
    }
    
    @Override public void save(ScriptMBean updatedBean)
    {
        jmxScript.save(updatedBean);
    }
    
    @Override public void setScope(Scriptable scope)
    {
        super.setScope(scope);
        
        // Delegate the same JavaScript Scope to the actual Java object that provides the "jmx" JavaScript object.
        jmxScript.setScope(getScope());
    }
}