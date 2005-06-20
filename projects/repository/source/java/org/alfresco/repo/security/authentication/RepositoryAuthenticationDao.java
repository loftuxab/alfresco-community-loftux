/*
 * Created on 13-Jun-2005
 * 
 * TODO Comment this class
 * 
 * 
 */
package org.alfresco.repo.security.authentication;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.acegisecurity.GrantedAuthority;
import net.sf.acegisecurity.GrantedAuthorityImpl;
import net.sf.acegisecurity.UserDetails;
import net.sf.acegisecurity.providers.dao.UsernameNotFoundException;
import net.sf.acegisecurity.providers.encoding.PasswordEncoder;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.search.QueryParameterDefImpl;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyTypeDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.cmr.search.QueryParameterDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.springframework.dao.DataAccessException;

public class RepositoryAuthenticationDao implements MutableAuthenticationDao
{
    public static final String SYSTEM_FOLDER = "/alf:system";
    public static final String PEOPLE_FOLDER  = SYSTEM_FOLDER + "/alf:people";
    
    private NodeService nodeService;
    private NamespacePrefixResolver namespacePrefixResolver;
    private DictionaryService dictionaryService;
    private PasswordEncoder passwordEncoder;
    
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException
    {
        //System.out.println("Getting user: "+userName);
        NodeRef userRef = getUserOrNull(userName);
        if(userRef == null)
        {
            throw new UsernameNotFoundException("Could not find user by userName: "+userName);
        }
        
        Map<QName, Serializable> properties = nodeService.getProperties(userRef);
        String password = ValueConverter.convert(String.class, properties.get(QName.createQName("alf", "password", namespacePrefixResolver))); 
        String salt = ValueConverter.convert(String.class, properties.get(QName.createQName("alf", "salt", namespacePrefixResolver))); 
        
        // TODO: Get roles correctly
        
        GrantedAuthority[] gas = new GrantedAuthority[1];
        gas[0] = new GrantedAuthorityImpl("ROLE_AUTHENTICATED");
        
        NodeRef personRef = getPersonOrNull(userName);
        if(personRef == null)
        {
            throw new UsernameNotFoundException("Could not find person by userName: "+userName);
        }
        
        //System.out.println("\tFound user: "+userName);
        RepositoryUserDetails ud = new RepositoryUser(userName, password, true, true, true, true, gas, salt, userRef, personRef);
        return ud;
    }

    /*package for testing*/  NodeRef getPersonOrNull(String userName)
    {
        NodeRef rootNode = nodeService.getRootNode(StoreContextHolder.getContext());
        QueryParameterDefinition[] defs = new QueryParameterDefinition[1];
        PropertyTypeDefinition text = dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT);
        defs[0] = new QueryParameterDefImpl(QName.createQName("alf", "var", namespacePrefixResolver), text, true, userName);
        List<ChildAssociationRef> results = nodeService.selectNodes(rootNode, PEOPLE_FOLDER + "/alf:person[@alf:userName = $alf:var ]", defs, namespacePrefixResolver, false);
        if(results.size() != 1)
        {
            return null;
        }
        return results.get(0).getChildRef();
    }

    /*package for testing*/ NodeRef getUserOrNull(String userName)
    {
        NodeRef rootNode = nodeService.getRootNode(StoreContextHolder.getContext());
        QueryParameterDefinition[] defs = new QueryParameterDefinition[1];
        PropertyTypeDefinition text = dictionaryService.getPropertyType(PropertyTypeDefinition.TEXT);
        defs[0] = new QueryParameterDefImpl(QName.createQName("alf", "var", namespacePrefixResolver), text, true, userName);
        List<ChildAssociationRef> results = nodeService.selectNodes(rootNode, PEOPLE_FOLDER + "/alf:user[@alf:username = $alf:var ]", defs, namespacePrefixResolver, false);
        if(results.size() != 1)
        {
            return null;
        }
        return results.get(0).getChildRef();
    }

    public void createUser(String userName, String rawPassword) throws AuthenticationException
    {
        NodeRef userRef = getUserOrNull(userName);
        if(userRef != null)
        {
            throw new AuthenticationException("User already exists: "+userName);
        }
        NodeRef typesNode = getOrCreateTypeLocation();
        Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
        properties.put(ContentModel.PROP_USER_USERNAME, userName);
        String salt = GUID.generate();
        properties.put(ContentModel.PROP_SALT, salt);
        properties.put(ContentModel.PROP_PASSWORD, passwordEncoder.encodePassword(rawPassword, salt));
        nodeService.createNode(typesNode, ContentModel.ASSOC_CHILDREN, ContentModel.TYPE_USER, ContentModel.TYPE_USER, properties);
    }
    

    private NodeRef getOrCreateTypeLocation()
    {
        NodeRef rootNode = nodeService.getRootNode(StoreContextHolder.getContext());
        List<ChildAssociationRef> results = nodeService.getChildAssocs(rootNode, QName.createQName("alf", "system", namespacePrefixResolver));
        NodeRef sysNode = null;
        if(results.size() == 0)
        {
            sysNode = nodeService.createNode(rootNode, ContentModel.ASSOC_CHILDREN, QName.createQName("alf", "system", namespacePrefixResolver), ContentModel.TYPE_CONTAINER ).getChildRef();
        }
        else
        {
            sysNode = results.get(0).getChildRef();
        }
        results = nodeService.getChildAssocs(sysNode, QName.createQName("alf", "people", namespacePrefixResolver));
        NodeRef typesNode = null;
        if(results.size() == 0)
        {
            typesNode = nodeService.createNode(sysNode, ContentModel.ASSOC_CHILDREN, QName.createQName("alf", "people", namespacePrefixResolver), ContentModel.TYPE_CONTAINER ).getChildRef();
        }
        else
        {
            typesNode = results.get(0).getChildRef();
        }
        return typesNode;
    }
    
    public void updateUser(String userName, String rawPassword) throws AuthenticationException
    {
        NodeRef userRef = getUserOrNull(userName);
        if(userRef == null)
        {
            throw new AuthenticationException("User does not exist: "+userName);
        }
        Map<QName, Serializable> properties = nodeService.getProperties(userRef);
        String salt = GUID.generate();
        properties.remove(ContentModel.PROP_SALT);
        properties.put(ContentModel.PROP_SALT, salt);
        properties.remove(ContentModel.PROP_PASSWORD);
        properties.put(ContentModel.PROP_PASSWORD, passwordEncoder.encodePassword(rawPassword, salt));
        nodeService.setProperties(userRef, properties);
    }

    public void deleteUser(String userName) throws AuthenticationException
    {
        NodeRef userRef = getUserOrNull(userName);
        if(userRef == null)
        {
            throw new AuthenticationException("User does not exist: "+userName);
        }
        nodeService.deleteNode(userRef);
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }
    

    public void setNamespaceService(NamespacePrefixResolver namespacePrefixResolver)
    {
        this.namespacePrefixResolver = namespacePrefixResolver;
    }
    

    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    

    public void setPasswordEncoder(PasswordEncoder passwordEncoder)
    {
        this.passwordEncoder = passwordEncoder;
    }
    

    
}
