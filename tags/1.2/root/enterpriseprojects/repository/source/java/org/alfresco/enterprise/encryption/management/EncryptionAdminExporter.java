/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.enterprise.encryption.management;

import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.RequiredModelMBean;

import org.alfresco.encryption.AlfrescoKeyStore;
import org.alfresco.encryption.ReEncryptor;
import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.jmx.export.MBeanExportOperations;
import org.springframework.jmx.export.annotation.AnnotationJmxAttributeSource;
import org.springframework.jmx.export.assembler.MBeanInfoAssembler;
import org.springframework.jmx.export.assembler.MetadataMBeanInfoAssembler;

/**
 * 
 * @since 4.0
 *
 */
public class EncryptionAdminExporter
{
    /** The JMX exporter. */
    private MBeanExportOperations exporter;

    private AlfrescoKeyStore mainKeyStore;
    private AlfrescoKeyStore sslKeyStore;
    private AlfrescoKeyStore sslTrustStore;

	private ReEncryptor reEncryptor;
    private MBeanInfoAssembler assembler;
    
    public EncryptionAdminExporter()
    {
        assembler = new MetadataMBeanInfoAssembler(new AnnotationJmxAttributeSource());
    }
    
	public void setMainKeyStore(AlfrescoKeyStore mainKeyStore)
	{
		this.mainKeyStore = mainKeyStore;
	}

	public void setSslKeyStore(AlfrescoKeyStore sslKeyStore)
	{
		this.sslKeyStore = sslKeyStore;
	}

	public void setSslTrustStore(AlfrescoKeyStore sslTrustStore)
	{
		this.sslTrustStore = sslTrustStore;
	}

	public void setReEncryptor(ReEncryptor reEncryptor)
	{
		this.reEncryptor = reEncryptor;
	}

	/**
     * Sets the JMX exporter.
     * 
     * @param exporter
     *            the JMX exporter
     */
    public void setJmxExporter(MBeanExportOperations exporter)
    {
        this.exporter = exporter;
    }
    
    protected String getObjectName()
    {
    	return "Alfresco:Name=Encryption";
    }

    protected String getKeyStoreObjectName(String keyStoreName)
    {
    	return "Alfresco:Name=Encryption,KeyStore=" + keyStoreName;
    }
    
    protected void registerAlfrescoKeyStoreBean(AlfrescoKeyStore keyStore)
    throws NullPointerException, RuntimeOperationsException, JMException, InvalidTargetObjectTypeException
    {
        String name = getKeyStoreObjectName(keyStore.getName());
    	ObjectName objectName = new ObjectName(name);
    	AlfrescoKeyStoreBean bean = new AlfrescoKeyStoreBean(keyStore);

	    ModelMBean mbean = new RequiredModelMBean();
        mbean.setModelMBeanInfo(this.assembler.getMBeanInfo(bean, name));
        mbean.setManagedResource(bean, "ObjectReference");
		this.exporter.registerManagedResource(mbean, objectName);
    }
    
    protected void registerEncryptionAdminBean()
    throws NullPointerException, RuntimeOperationsException, MBeanException, JMException, InvalidTargetObjectTypeException
    {
		EncryptionAdmin encryptionAdmin = new EncryptionAdmin(reEncryptor, mainKeyStore);
    	String name = getObjectName();
    	ObjectName objectName = new ObjectName(name);
	    ModelMBean mbean = new RequiredModelMBean();
        mbean.setModelMBeanInfo(this.assembler.getMBeanInfo(encryptionAdmin, name));
        mbean.setManagedResource(encryptionAdmin, "ObjectReference");
		this.exporter.registerManagedResource(mbean, objectName);
    }

    public void init()
    {
    	try
    	{
    		registerEncryptionAdminBean();
			registerAlfrescoKeyStoreBean(mainKeyStore);
			registerAlfrescoKeyStoreBean(sslKeyStore);
			registerAlfrescoKeyStoreBean(sslTrustStore);
    	}
    	catch(Exception e)
    	{
    		throw new AlfrescoRuntimeException("Unable to initialise encryption admin mbeans", e);
    	}
    }

}
