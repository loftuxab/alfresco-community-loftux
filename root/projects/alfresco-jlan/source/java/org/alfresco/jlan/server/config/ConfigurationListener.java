package org.alfresco.jlan.server.config;

/*
 * ConfigurationListener.java
 *
 * Copyright (c) 2004 Starlasoft. All rights reserved.
 */
 
/**
 * Server Configuration Listener Interface
 * 
 * <p>The configuration listener receives server configuration change notifications that can be used to provide dynamic
 * updating of various server components.
 * 
 * <p>The configuration listener may throw an InvalidConfigurationException if the updated value is invalid or there is
 * a problem during the dynamic component update. The listener also returns a status to indicate if it ignored the update,
 * a server restart is required or the change was accepted.
 */
public interface ConfigurationListener {

	//	Configuration listener status codes
	
	public static final int StsIgnored					= 0;
	public static final int StsAccepted					= 1;
	public static final int StsNewSessionsOnly	= 2;
	public static final int StsRestartRequired	= 3;
	
	/**
	 * Configuration variable changed
	 * 
	 * @param id int
	 * @param config ServerConfiguration
	 * @param newVal Object
	 * @return int
	 * @exception InvalidConfigurationException
	 */
	public int configurationChanged(int id, ServerConfiguration config, Object newVal)
		throws InvalidConfigurationException;
}
