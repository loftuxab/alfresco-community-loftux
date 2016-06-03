package org.apache.solr.core;

import java.util.Properties;

/**
 * This class was created solely for the purpose of exposing the coreProperties of the CoreDescriptor
 * @author Ahmed Owian
 */
public class CoreDescriptorDecorator {
	private CoreDescriptor descriptor;

	public CoreDescriptorDecorator(CoreDescriptor descriptor)
	{
		this.descriptor = descriptor;
	}
	
	public Properties getCoreProperties()
	{
		return this.descriptor.coreProperties;
	}
}
