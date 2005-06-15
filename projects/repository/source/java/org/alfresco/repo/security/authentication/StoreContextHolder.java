/*
 * Created on 14-Jun-2005
 *
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.security.authentication;

import org.alfresco.service.cmr.repository.StoreRef;

public class StoreContextHolder extends RepositoryAuthenticationDao
{
    private static ThreadLocal<StoreRef> contextHolder = new ThreadLocal<StoreRef>();

    public static void setContext(StoreRef store) {
        contextHolder.set(store);
    }

    public static StoreRef getContext() {
        return contextHolder.get();
    }
    
}
