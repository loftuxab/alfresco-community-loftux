package org.alfresco.enterprise.repo.content.cryptodoc;

import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class JceInfo {
	
	@Test
	public void getAlgorithms() {
		Map<String, Set<String>> typeAlgorithms = new HashMap<String, Set<String>>(10);
		for (Provider provider : Security.getProviders()) {
			for (Service service : provider.getServices()) {
				if (!typeAlgorithms.containsKey(service.getType()))
					typeAlgorithms.put(service.getType(), new HashSet<String>(20));
				
				Set<String> algorithms = typeAlgorithms.get(service.getType());
				algorithms.add(service.getAlgorithm());
			}
		}
		
		System.out.println(typeAlgorithms);
	}

}
