package org.alfresco.repo.policy;

import java.util.Collection;

/**
 * @author David Caruana
 */
/*package*/ interface PolicyList<P extends Policy>
{
	/**
	 * @return the set of policies within this policy set
	 */
	public Collection<P> getPolicies();
}
