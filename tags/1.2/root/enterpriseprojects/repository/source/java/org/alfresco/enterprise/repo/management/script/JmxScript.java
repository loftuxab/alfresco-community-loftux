/*
 * Copyright 2005-2013 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management.script;

import javax.management.MBeanServerConnection;

import org.alfresco.repo.jscript.Scopeable;
import org.mozilla.javascript.Scriptable;

/**
 * This interface defines the operations which are possible via the JavaScript root object "jmx".
 * 
 * @author Neil Mc Erlean
 * @since 4.2
 */
public interface JmxScript extends Scopeable
{
    /**
     * This method wraps the JMX-standard method for querying MBeans. Please refer to that method for full documentation.
     * 
     * @param objectName The object name pattern identifying the MBeans to be retrieved.
     *                   If <tt>null</tt> or no domain and key properties are specified, all the MBeans registered will be retrieved.
     * @return an array of script-friendly bean objects. If there are no matching results, an empty array is returned.
     * 
     * @see MBeanServerConnection#queryMBeans(javax.management.ObjectName, javax.management.QueryExp)
     */
    ScriptMBean[] queryMBeans(String objectName);
    
    /**
     * This method should be called to save (persist) attribute value changes for the specified {@link ScriptMBean MBean}.
     * @param updatedBean the MBean whose attributes have been updated.
     */
    void save(ScriptMBean updatedBean);
    
    // I don't think we should have to redeclare this method here as it is already declared in Scopeable. However, we do so otherwise
    // the security interceptors do not 'see' the method and hence won't allow it to be declared as ACL_ALLOW.
    void setScope(Scriptable scope);
}