/**
 * 
 */
package org.alfresco.rm;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.alfresco.repo.jscript.BaseScriptImplementation;
import org.alfresco.repo.jscript.Scopeable;
import org.alfresco.repo.jscript.ValueConverter;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.mozilla.javascript.Scriptable;

/**
 * Script implementation containing the commonly used record management functions.
 * 
 * @author Roy Wetherall
 */
public class RecordsManagementScript extends BaseScriptImplementation implements Scopeable 
{
	/** Scriptable scope object */
	private Scriptable scope;
	
	/** The service registry */
	private ServiceRegistry services;
	
	/**
	 * Set the service registry
	 * 
	 * @param services	the service registry
	 */
	public void setServiceRegistry(ServiceRegistry services) 
	{
		this.services = services;
	}
	
	/**
	 * Set the scope
	 * 
	 * @param scope	the script scope
	 */
	public void setScope(Scriptable scope) 
	{
		this.scope = scope;
	}
	
	/**
	 * Calculates the next interval date for a given type of date interval.
	 * 
	 * @param reviewPeriod	review period catetegory value as a node
	 * @param fromDate	    the date from which the next interval date should be calculated
	 * @return				the next interval date
	 */
	public Serializable calculateDateInterval(Serializable reviewPeriod, Serializable fromDate)
	{
		ValueConverter valueConverter = new ValueConverter();
		Date date = (Date)valueConverter.convertValueForRepo(fromDate);
		NodeRef nodeRef = (NodeRef)valueConverter.convertValueForRepo(reviewPeriod);
		
		Calendar calendar = Calendar.getInstance();		
		calendar.setTime(date);
		
		if (nodeRef.getId().equals("rm:reviewPeriod-2") == true) 
		{
			// Bi-Annual calculation
			calendar.add(Calendar.YEAR, 2);
		} 
		else if (nodeRef.getId().equals("rm:reviewPeriod-3") == true || 
				 nodeRef.getId().equals("rm:reviewPeriod-4") == true || 
				 nodeRef.getId().equals("rm:reviewPeriod-5") == true) 
		{
			// Annual calculation
			// TODO: Proper calculation of fiscal year end (US Govt) and Calendar Year End
			calendar.add(Calendar.YEAR, 1);
		} 
		else if (nodeRef.getId().equals("rm:reviewPeriod-6") == true) 
		{
			// Semi-annual calculation
			calendar.add(Calendar.MONTH, 6);
		} 
		else if (nodeRef.getId().equals("rm:reviewPeriod-7") == true) 
		{
			// Quaterly calculation
			calendar.add(Calendar.MONTH, 3);
		} 
		else if (nodeRef.getId().equals("rm:reviewPeriod-8") == true) 
		{
			// Monthly calculation
			calendar.add(Calendar.MONTH, 1);
		} 
		else if (nodeRef.getId().equals("rm:reviewPeriod-9") == true) 
		{
			// Weekly calculation
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
		} 
		else if (nodeRef.getId().equals("rm:reviewPeriod-10") == true) 
		{
			// Daily calculation
			calendar.add(Calendar.DAY_OF_YEAR, 1);
		} 
		
		return valueConverter.convertValueForScript(services, scope, null, calendar.getTime());
	}
}
