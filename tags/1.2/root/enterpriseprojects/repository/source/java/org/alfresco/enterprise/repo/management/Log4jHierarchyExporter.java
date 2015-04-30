/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.management.Attribute;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.alfresco.repo.domain.schema.SchemaAvailableEvent;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

/**
 * In response to a {@link SchemaAvailableEvent} on server startup (immediately after the database schema is available),
 * exports a Log4J HierarchyDynamicMBean and automatically registers all currently active loggers as MBeans. Further
 * loggers can be added through the MBean's interface. This is done through reflection to avoid compile-time
 * dependencies against log4j, which is an optional component.
 * 
 * @author dward
 */
public class Log4jHierarchyExporter extends AbstractManagedResourceExporter<SchemaAvailableEvent>
{
    private static final Log logger = LogFactory.getLog(Log4jHierarchyExporter.class);

    private Class<?> hierarchyClass;
    private Object hierarchy;

    /**
     * Instantiates a new log4j hierarchy exporter.
     */
    public Log4jHierarchyExporter()
    {

        super(SchemaAvailableEvent.class);
        try
        {
            // Create a HierarchyDynamicMBean and look up all the classes and methods we need
            this.hierarchyClass = Class.forName("org.apache.log4j.jmx.HierarchyDynamicMBean");
        }
        catch (Exception e)
        {
            // Non-fatal. Log4j is an optional runtime dependency
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.alfresco.enterprise.repo.management.AbstractManagedResourceExporter#getObjectsToExport(org.springframework
     * .context.ApplicationEvent)
     */
    @Override
    public Map<ObjectName, ?> getObjectsToExport(SchemaAvailableEvent event) throws MalformedObjectNameException
    {
        if (this.hierarchyClass != null)
        {
            try
            {
                // We're going to have to work around a bug in log4j, where the threshold property is declared as a
                // string, but not returned as such! We wrap the log4j MBean with a dynamic proxy.
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                this.hierarchy = this.hierarchyClass.newInstance();
                ProxyFactory proxyFactory = new ProxyFactory(this.hierarchy);

                // Override getAttribute()
                NameMatchMethodPointcutAdvisor getAttributeAdvisor = new NameMatchMethodPointcutAdvisor(
                        new MethodInterceptor()
                        {

                            public Object invoke(MethodInvocation invocation) throws Throwable
                            {
                                Object result = invocation.proceed();
                                // Convert all attributes to Strings, because they are declared as such!
                                return result.toString();
                            }
                        });
                getAttributeAdvisor.addMethodName("getAttribute");
                proxyFactory.addAdvisor(getAttributeAdvisor);

                // Override getAttributes()
                NameMatchMethodPointcutAdvisor getAttributesAdvisor = new NameMatchMethodPointcutAdvisor(
                        new MethodInterceptor()
                        {

                            @SuppressWarnings("unchecked")
                            public Object invoke(MethodInvocation invocation) throws Throwable
                            {
                                List<Attribute> result = (List<Attribute>) invocation.proceed();
                                ListIterator<Attribute> i = (ListIterator<Attribute>) result.iterator();
                                while (i.hasNext())
                                {
                                    Attribute a = i.next();
                                    // Convert the threshold attribute to a String
                                    if (a.getName().equals("threshold"))
                                    {
                                        i.set(new Attribute(a.getName(), a.getValue().toString()));
                                    }
                                }
                                return result;
                            }
                        });
                getAttributeAdvisor.addMethodName("getAttributes");
                proxyFactory.addAdvisor(getAttributesAdvisor);

                return Collections.singletonMap(new ObjectName("Alfresco:Name=Log4jHierarchy"), proxyFactory
                        .getProxy(classLoader));
            }
            catch (Exception e)
            {
                // Non-fatal. Log4j is an optional runtime dependency
            }
        }
        return Collections.emptyMap();
    }

    /*
     * (non-Javadoc)
     * @see org.alfresco.enterprise.repo.management.AbstractManagedResourceExporter#postRegister(java.lang.Object)
     */
    @Override
    protected void postRegister()
    {
        if (this.hierarchy != null)
        {
            try
            {
                // We can only add logger MBeans after the hierarchy bean has been registered, because it maintains a
                // reference to the MBean server
                Method addLoggerMethod = this.hierarchyClass.getMethod("addLoggerMBean", String.class);
                Class<?> logManagerClass = Class.forName("org.apache.log4j.LogManager");
                Class<?> loggerClass = Class.forName("org.apache.log4j.Logger");
                Method getNameMethod = loggerClass.getMethod("getName");

                // Export the root logger
                Object logger = logManagerClass.getMethod("getRootLogger").invoke(null);
                Object name = getNameMethod.invoke(logger);
                addLoggerMethod.invoke(this.hierarchy, name);

                // Export all the other current loggers
                Enumeration<?> loggers = (Enumeration<?>) logManagerClass.getMethod("getCurrentLoggers").invoke(null);
                while (loggers.hasMoreElements())
                {
                    logger = loggers.nextElement();
                    name = getNameMethod.invoke(logger);
                    addLoggerMethod.invoke(this.hierarchy, name);
                }

            }
            catch (Exception e)
            {
                Log4jHierarchyExporter.logger.warn("Error adding logger MBeans", e);
            }
            this.hierarchy = null; // No need to hold on to reference
        }
    }
}
