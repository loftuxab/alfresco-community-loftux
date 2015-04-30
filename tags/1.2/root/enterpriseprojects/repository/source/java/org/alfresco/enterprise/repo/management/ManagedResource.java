/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.management;

import javax.management.ObjectName;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.RequiredModelMBean;

import org.alfresco.repo.management.ManagedBean;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.MBeanExportOperations;
import org.springframework.jmx.export.assembler.InterfaceBasedMBeanInfoAssembler;
import org.springframework.jmx.support.JmxUtils;

/**
 * Describes an object to be exported as a JMX MBean. A more extensible alternative to Spring's
 * {@link InterfaceBasedMBeanInfoAssembler} that doesn't require all resources to be registered at once in a single
 * bean. The set of management interfaces to expose can either be controlled explicitly with
 * {@link #setManagedInterfaces(Class[])} or otherwise will be determined automatically according to the interfaces
 * implemented by the supplied object.
 * 
 * @author dward
 */
public class ManagedResource implements InitializingBean, BeanNameAware, ManagedBean
{
    /**
     * An optional suffix that can be used on this objects bean name to distinguish it from the name of the bean that it
     * 'wraps'.
     */
    private static final String NAME_SUFFIX = "Resource";

    /** Constant for the JMX <code>mr_type</code> "ObjectReference". */
    private static final String MR_TYPE_OBJECT_REFERENCE = "ObjectReference";

    /** The bean name. */
    private String beanName;

    /** An optional object name. */
    private String objectName;

    /** The exporter. */
    private MBeanExportOperations exporter;

    /** The resource to be exported. */
    private Object resource;

    /** The assembler. */
    private InterfaceBasedMBeanInfoAssembler assembler;

    /**
     * Instantiates a new managed resource.
     */
    public ManagedResource()
    {
        this.assembler = new InterfaceBasedMBeanInfoAssembler();
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String name)
    {
        this.beanName = name.endsWith(ManagedResource.NAME_SUFFIX) ? name.substring(0, name.length()
                - ManagedResource.NAME_SUFFIX.length()) : name;
    }

    /**
     * Sets the JMX object name. If not specified, this will be derived from the bean name.
     * 
     * @param objectName
     *            the object name
     */
    public void setObjectName(String objectName)
    {
        this.objectName = objectName;
    }

    /**
     * Sets the JMX exporter object.
     * 
     * @param exporter
     *            the JMX exporter
     */
    public void setJmxExporter(MBeanExportOperations exporter)
    {
        this.exporter = exporter;
    }

    /**
     * Sets the managed interfaces. If not specified, these will be determined automatically from the interfaces
     * implemented by the resource. Note that the resource need not even implement these interfaces; it need only expose
     * methods whose signatures match those of methods in the interface.
     * 
     * @param interfaces
     *            the managed interfaces
     */
    public void setManagedInterfaces(Class<?>[] interfaces)
    {
        this.assembler.setManagedInterfaces(interfaces);
    }

    /**
     * Sets the resource to export. If this is not a compliant MBean it will be 'wrapped' as one.
     * 
     * @param resource
     *            the resource to set
     */
    public void setResource(Object resource)
    {
        this.resource = resource;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception
    {
        this.assembler.afterPropertiesSet();
        String beanKey = this.objectName == null ? "Alfresco:Name=" + this.beanName : this.objectName;
        Object resource;
        if (!JmxUtils.isMBean(this.resource.getClass()))
        {
            ModelMBean mbean = new RequiredModelMBean();
            mbean.setModelMBeanInfo(this.assembler.getMBeanInfo(this.resource, beanKey));
            mbean.setManagedResource(this.resource, ManagedResource.MR_TYPE_OBJECT_REFERENCE);
            resource = mbean;
        }
        else
        {
            resource = this.resource;
        }
        this.exporter.registerManagedResource(resource, new ObjectName(beanKey));
    }
}
