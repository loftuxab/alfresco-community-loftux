package org.alfresco.solr.data;

import java.util.HashSet;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.security.PermissionService;

/**
 * Statically configured set of authorities that always have read access.
 * 
 * @author Matt Ward
 */
public class GlobalReaders
{
    private static HashSet<String> readers = new HashSet<String>();
    
    static
    {        
        readers.add(PermissionService.OWNER_AUTHORITY);
        readers.add(PermissionService.ADMINISTRATOR_AUTHORITY);
        readers.add(AuthenticationUtil.getSystemUserName());
    }
    
    public static HashSet<String> getReaders()
    {
        return readers;
    }
}
