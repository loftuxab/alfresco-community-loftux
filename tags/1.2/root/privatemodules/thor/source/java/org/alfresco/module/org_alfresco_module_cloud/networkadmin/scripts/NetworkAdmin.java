package org.alfresco.module.org_alfresco_module_cloud.networkadmin.scripts;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_cloud.CloudModel;
import org.alfresco.query.PagingRequest;
import org.alfresco.repo.jscript.BaseScopableProcessorExtension;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.security.PersonService.PersonInfo;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.Pair;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class NetworkAdmin extends BaseScopableProcessorExtension
{
	private PersonService personService;
	private NodeService nodeService;
    private ServiceRegistry serviceRegistry;

	public void setServiceRegistry(ServiceRegistry serviceRegistry)
	{
		this.serviceRegistry = serviceRegistry;
		this.nodeService = serviceRegistry.getNodeService();
		this.personService = serviceRegistry.getPersonService();
	}

	public Scriptable getPeople(String nameFilter, String sortBy, int skipCount, int maxItems, boolean internal, boolean networkAdmin)
	{
		// Build the filter
		List<Pair<QName,String>> filter = new ArrayList<Pair<QName,String>>();
		filter.add(new Pair<QName, String>(ContentModel.PROP_FIRSTNAME, nameFilter));
		filter.add(new Pair<QName, String>(ContentModel.PROP_LASTNAME, nameFilter));
		filter.add(new Pair<QName, String>(ContentModel.PROP_USERNAME, nameFilter));

		// Build the sorting. The user controls the primary sort, we supply
		// additional ones automatically
		List<Pair<QName,Boolean>> sort = new ArrayList<Pair<QName,Boolean>>();
		if ("lastName".equals(sortBy))
		{
			sort.add(new Pair<QName, Boolean>(ContentModel.PROP_LASTNAME, true));
			sort.add(new Pair<QName, Boolean>(ContentModel.PROP_FIRSTNAME, true));
			sort.add(new Pair<QName, Boolean>(ContentModel.PROP_USERNAME, true));
		}
		else if ("firstName".equals(sortBy))
		{
			sort.add(new Pair<QName, Boolean>(ContentModel.PROP_FIRSTNAME, true));
			sort.add(new Pair<QName, Boolean>(ContentModel.PROP_LASTNAME, true));
			sort.add(new Pair<QName, Boolean>(ContentModel.PROP_USERNAME, true));
		}
		else
		{
			sort.add(new Pair<QName, Boolean>(ContentModel.PROP_USERNAME, true));
			sort.add(new Pair<QName, Boolean>(ContentModel.PROP_FIRSTNAME, true));
			sort.add(new Pair<QName, Boolean>(ContentModel.PROP_LASTNAME, true));
		}

		List<NodeRef> ret = new ArrayList<NodeRef>(maxItems);

		// Keep looping until we run out of people or we hit maxItems
		while(true)
		{
			PagingRequest paging = new PagingRequest(skipCount, maxItems);
			List<PersonInfo> people = personService.getPeople(filter, true, sort, paging).getPage();
			if(people.size() == 0)
			{
				break;
			}

	//		for (int i=0; i<users.length; i++)
	//		{
	//			PersonInfo person = people.get(i);
	//			users[i] = new ScriptUser(person.getUserName(), person.getNodeRef(), serviceRegistry, this.getScope());
	//		}

			for(PersonInfo person : people)
			{
				if(ret.size() >= maxItems)
				{
					break;
				}

				// Assume initially that the next ScriptNode does pass the filters.
				boolean passesFilters = true;
	
				if(internal && nodeService.hasAspect(person.getNodeRef(), CloudModel.ASPECT_EXTERNAL_PERSON))
				{
					passesFilters = false;
				}
	
				if(networkAdmin && !nodeService.hasAspect(person.getNodeRef(), CloudModel.ASPECT_NETWORK_ADMIN))
				{
					passesFilters = false;
				}
	
				if(passesFilters)
				{
					ret.add(person.getNodeRef());
				}
			}
			
			if(ret.size() >= maxItems)
			{
				break;
			}

			skipCount += maxItems;
		}

		return Context.getCurrentContext().newArray(getScope(), ret.toArray());
	}
}
