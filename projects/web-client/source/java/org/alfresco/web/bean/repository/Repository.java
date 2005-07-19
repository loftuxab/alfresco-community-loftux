/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.bean.repository;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.RepositoryAuthenticationDao;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceException;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.ui.common.Utils;
import org.apache.log4j.Logger;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Helper class for accessing repository objects, convert values, escape values and filetype icons.
 * 
 * @author gavinc
 * @author kevinr
 */
public final class Repository
{
   public static final String ERROR_NODEREF = "Unable to find the repository item referenced by Id: {0} - the record has probably been deleted from the database.";
   public static final String ERROR_GENERIC = "A system error occured during the operation: {0}";
   public static final String ERROR_NOHOME  = "The Home Space node referenced by Id: {0} cannot be found. It may have been deleted from the database. Please contact your system administrator.";
   public static final String ERROR_SEARCH  = "Search failed due to system error: {0}";
   
   private static final String IMAGE_PREFIX16 = "/images/filetypes/";
   private static final String IMAGE_PREFIX32 = "/images/filetypes32/";
   private static final String IMAGE_POSTFIX = ".gif";
   private static final String DEFAULT_FILE_IMAGE16 = IMAGE_PREFIX16 + "_default" + IMAGE_POSTFIX;
   private static final String DEFAULT_FILE_IMAGE32 = IMAGE_PREFIX32 + "_default" + IMAGE_POSTFIX;
   
   private static Logger logger = Logger.getLogger(Repository.class);
   private static final Map<String, String> s_fileExtensionMap = new HashMap<String, String>(89, 1.0f);
   
   /** cache of client StoreRef */
   private static StoreRef storeRef = null;
   
   /** reference to Person folder */
   private static NodeRef peopleRef = null;
   
   /** reference to System folder */
   private static NodeRef systemRef = null;
   
   /** reference to the namespace service */
   private static NamespaceService namespaceService = null;
   
   /**
    * Private constructor
    */
   private Repository()
   {
   }
   
   /**
    * Returns a store reference object
    * 
    * @return A StoreRef object
    */
   public static StoreRef getStoreRef()
   {
      return storeRef;
   }
   
   /**
    * Returns a store reference object.
    * This method is used to setup the cached value by the ContextListener initialisation methods
    * 
    * @return The StoreRef object
    */
   public static StoreRef getStoreRef(ServletContext context)
   {
      storeRef = new StoreRef(Application.getRepositoryStoreUrl(context));
      
      return storeRef;
   }

   /**
    * Helper to get the display name for a Node.
    * The method will attempt to use the "name" attribute, if not found it will revert to using
    * the QName.getLocalName() retrieved from the primary parent relationship.
    * 
    * @param ref     NodeRef
    * 
    * @return display name string for the specified Node.
    */
   public static String getNameForNode(NodeService nodeService, NodeRef ref)
   {
      String name;
      
      // try to find a display "name" property for this node
      Object nameProp = nodeService.getProperty(ref, ContentModel.PROP_NAME);
      if (nameProp != null)
      {
         name = nameProp.toString();
      }
      else
      {
         // revert to using QName if not found
         name = nodeService.getPrimaryParent(ref).getQName().getLocalName();
      }
      
      return name;
   }

   /**
    * Escape a QName value so it can be used in lucene search strings
    * 
    * @param qName      QName to escape
    * 
    * @return escaped value
    */
   public static String escapeQName(QName qName)
   {
       String string = qName.toString();
       StringBuilder buf = new StringBuilder(string.length() + 4);
       for (int i = 0; i < string.length(); i++)
       {
           char c = string.charAt(i);
           if ((c == '{') || (c == '}') || (c == ':') || (c == '-'))
           {
              buf.append('\\');
           }
   
           buf.append(c);
       }
       return buf.toString();
   }

   /**
    * Return whether a Node is current Locked
    * 
    * @param node             The Node wrapper to test against
    * @param lockService      The LockService to use
    * 
    * @return whether a Node is current Locked
    */
   public static Boolean isNodeLocked(Node node, LockService lockService)
   {
      Boolean locked = Boolean.FALSE;
      if (node.hasAspect(ContentModel.ASPECT_LOCKABLE))
      {
         LockStatus lockStatus = lockService.getLockStatus(node.getNodeRef());
         if (lockStatus == LockStatus.LOCKED || lockStatus == LockStatus.LOCK_OWNER)
         {
            locked = Boolean.TRUE;
         }
      }
      
      return locked;
   }
   
   /**
    * Return whether a Node is current Locked and this user is the owner of the lock
    * 
    * @param node             The Node wrapper to test against
    * @param lockService      The LockService to use
    * 
    * @return whether a Node is current Locked
    */
   public static Boolean isNodeLockOwner(Node node, LockService lockService)
   {
      Boolean locked = Boolean.FALSE;
      if (node.hasAspect(ContentModel.ASPECT_LOCKABLE))
      {
         LockStatus lockStatus = lockService.getLockStatus(node.getNodeRef());
         if (lockStatus == LockStatus.LOCK_OWNER)
         {
            locked = Boolean.TRUE;
         }
      }
      
      return locked;
   }
   
   /**
    * Return the human readable form of the specified node Path
    * 
    * @param path    Path to extract readable form from, excluding the final element
    * 
    * @return human readable form of the Path excluding the final element
    */
   public static String getDisplayPath(Path path)
   {
      StringBuilder buf = new StringBuilder(64);
      
      // construct the path to this Node
      for (int i=0; i<path.size()-1; i++)
      {
         String elementString;
         Path.Element element = path.get(i);
         if (element instanceof Path.ChildAssocElement)
         {
            ChildAssociationRef elementRef = ((Path.ChildAssocElement)element).getRef();
            if (elementRef.getParentRef() == null)
            {
               elementString = "/";
            }
            else
            {
               elementString = elementRef.getQName().getLocalName().replace('_', ' ');
            }
         }
         else
         {
            elementString = element.getElementString();
         }
         
         if (buf.length() > 1)
         {
            buf.append('/');
         }
         buf.append(elementString);
      }
      
      return buf.toString();
   }
   
   /**
    * Resolve a Path by converting each element into its display NAME attribute
    * 
    * @param path       Path to convert
    * @param separator  Separator to user between path elements
    * @param prefix     To prepend to the path
    * 
    * @return Path converted using NAME attribute on each element
    */
   public static String getNamePath(NodeService nodeService, Path path, NodeRef rootNode, String separator, String prefix)
   {
      StringBuilder buf = new StringBuilder(128);
      
      // ignore root node check if not passed in
      boolean foundRoot = (rootNode == null);
      
      buf.append(prefix);
      
      // skip first element as it represents repo root '/'
      for (int i=1; i<path.size(); i++)
      {
         Path.Element element = path.get(i);
         String elementString = null;
         if (element instanceof Path.ChildAssocElement)
         {
            ChildAssociationRef elementRef = ((Path.ChildAssocElement)element).getRef();
            if (elementRef.getParentRef() != null)
            {
               // only append if we've found the root already
               if (foundRoot == true)
               {
                  Object nameProp = nodeService.getProperty(elementRef.getChildRef(), ContentModel.PROP_NAME);
                  if (nameProp != null)
                  {
                     elementString = nameProp.toString();
                  }
                  else
                  {
                     elementString = element.getElementString();
                  }
               }
               
               // either we've found root already or may have now
               // check after as we want to skip the root as it represents the CIFS share name
               foundRoot = (foundRoot || elementRef.getChildRef().equals(rootNode));
            }
         }
         else
         {
            elementString = element.getElementString();
         }
         
         if (elementString != null)
         {
            buf.append(separator);
            buf.append(elementString);
         }
      }
      
      return buf.toString();
   }

   /**
    * Return the image path to the filetype icon for the specified node
    * 
    * @param node       Node to build filetype icon path for
    * @param small      True for the small 16x16 icon or false for the large 32x32 
    * 
    * @return the image path for the specified node type or the default icon if not found
    */
   public static String getFileTypeImage(Node node, boolean small)
   {
      String image = (small ? DEFAULT_FILE_IMAGE16 : DEFAULT_FILE_IMAGE32);
      
      String name = node.getName();
      int extIndex = name.lastIndexOf('.');
      if (extIndex != -1 && name.length() > extIndex + 1)
      {
         String ext = name.substring(extIndex + 1).toLowerCase();
         String key = ext + ' ' + (small ? "16" : "32");
         
         // found file extension for appropriate size image
         synchronized (s_fileExtensionMap)
         {
            image = s_fileExtensionMap.get(key);
            if (image == null)
            {
               // not found create for first time
               image = (small ? IMAGE_PREFIX16 : IMAGE_PREFIX32) + ext + IMAGE_POSTFIX;
               
               // does this image exist on the web-server?
               if (FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(image) != null)
               {
                  // found the image for this extension - save it for later
                  s_fileExtensionMap.put(key, image);
               }
               else
               {
                  // not found, save the default image for this extension instead
                  image = (small ? DEFAULT_FILE_IMAGE16 : DEFAULT_FILE_IMAGE32);
                  s_fileExtensionMap.put(key, image);
               }
            }
         }
      }
      
      return image;
   }

   /**
    * Return the mimetype code for the specified file name.
    * <p>
    * The file extension will be extracted from the filename and used to lookup the mimetype.
    * 
    * @param context       FacesContext
    * @param filename      Non-null filename to process
    * 
    * @return mimetype for the specified filename - falls back to 'application/octet-stream' if not found.
    */
   public static String getMimeTypeForFileName(FacesContext context, String filename)
   {
      // base the mimetype from the file extension
      MimetypeService mimetypeService = (MimetypeService)getServiceRegistry(context).getMimetypeService();
      
      // fall back to binary mimetype if no match found
      String mimetype = "application/octet-stream";
      int extIndex = filename.lastIndexOf('.');
      if (extIndex != -1)
      {
         String ext = filename.substring(extIndex + 1).toLowerCase();
         String mt = mimetypeService.getMimetypesByExtension().get(ext);
         if (mt != null)
         {
            mimetype = mt;
         }
      }
      
      return mimetype;
   }

   /**
    * Return a UserTransaction instance
    * 
    * @param context    FacesContext
    * 
    * @return UserTransaction
    */
   public static UserTransaction getUserTransaction(FacesContext context)
   {
      return getServiceRegistry(context).getUserTransaction();
   }

   /**
    * Return the Repository Service Registry
    * 
    * @param context Faces Context
    * @return the Service Registry
    */
   public static ServiceRegistry getServiceRegistry(FacesContext context)
   {
       return (ServiceRegistry)FacesContextUtils.getRequiredWebApplicationContext(
               context).getBean(ServiceRegistry.SERVICE_REGISTRY);
   }
   
   /**
    * Query a list of Person type nodes from the repo
    * It is currently assumed that all Person nodes exist below the Repository root node
    * 
    * @param context Faces Context
    * @param nodeService The node service
    * @return List of Person node objects
    */
   public static List<Node> getUsers(FacesContext context, NodeService nodeService)
   {
      List<Node> personNodes = null;
      
      UserTransaction tx = null;
      try
      {
         tx = Repository.getUserTransaction(context);
         tx.begin();
         
         NodeRef peopleRef = getSystemPeopleFolderRef(context, nodeService);
         
         // TODO: better to perform an XPath search or a get for a specific child type here?
         List<ChildAssociationRef> childRefs = nodeService.getChildAssocs(peopleRef);
         personNodes = new ArrayList<Node>(childRefs.size());
         for (ChildAssociationRef ref: childRefs)
         {
            // create our Node representation from the NodeRef
            NodeRef nodeRef = ref.getChildRef();
            
            if (nodeService.getType(nodeRef).equals(ContentModel.TYPE_PERSON))
            {
               // create our Node representation
               MapNode node = new MapNode(nodeRef, nodeService);
               
               // set data binding properties
               // this will also force initialisation of the props now during the UserTransaction
               // it is much better for performance to do this now rather than during page bind
               Map<String, Object> props = node.getProperties(); 
               props.put("fullName", ((String)props.get("firstName")) + ' ' + ((String)props.get("lastName")));
               String homeFolderId = (String)props.get("homeFolder");
               if (homeFolderId != null)
               {
                  props.put("homeSpace", new NodeRef(Repository.getStoreRef(), homeFolderId));
               }
               
               personNodes.add(node);
            }
         }
         
         // commit the transaction
         tx.commit();
      }
      catch (InvalidNodeRefException refErr)
      {
         Utils.addErrorMessage( MessageFormat.format(ERROR_NODEREF, new Object[] {"root"}) );
         personNodes = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      catch (Exception err)
      {
         Utils.addErrorMessage( MessageFormat.format(ERROR_GENERIC, err.getMessage()), err );
         personNodes = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      
      return personNodes;
   }
   
   /**
    * Return a reference to the special system folder
    * 
    * @param context
    * 
    * @return NodeRef to System folder
    */
   public static NodeRef getSystemFolderRef(FacesContext context, NodeService nodeService)
   {
      if (systemRef == null)
      {
         // get a reference to the system types folder node
         DynamicNamespacePrefixResolver resolver = new DynamicNamespacePrefixResolver(null);
         resolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
         
         List<NodeRef> results = nodeService.selectNodes(
               nodeService.getRootNode(Repository.getStoreRef()),
               RepositoryAuthenticationDao.SYSTEM_FOLDER,
               null,
               resolver,
               false);
         
         if (results.size() != 1)
         {
            throw new AlfrescoRuntimeException("Unable to find system folder: " + RepositoryAuthenticationDao.PEOPLE_FOLDER);
         }
         
         systemRef = results.get(0);
      }
      
      return systemRef;
   }
   
   /**
    * Return a reference to the special system folder containing Person instances
    * 
    * @param context
    * 
    * @return NodeRef to Person folder
    */
   public static NodeRef getSystemPeopleFolderRef(FacesContext context, NodeService nodeService)
   {
      if (peopleRef == null)
      {
         // get a reference to the system/people folder node
         DynamicNamespacePrefixResolver resolver = new DynamicNamespacePrefixResolver(null);
         resolver.addDynamicNamespace(NamespaceService.ALFRESCO_PREFIX, NamespaceService.ALFRESCO_URI);
         
         List<NodeRef> results = nodeService.selectNodes(
               nodeService.getRootNode(Repository.getStoreRef()),
               RepositoryAuthenticationDao.PEOPLE_FOLDER,
               null,
               resolver,
               false);
         
         if (results.size() != 1)
         {
            throw new AlfrescoRuntimeException("Unable to find system/people folder: " + RepositoryAuthenticationDao.PEOPLE_FOLDER);
         }
         
         peopleRef = results.get(0);
      }
      
      return peopleRef;
   }
   
   /**
    * Creates a QName representation for the given String.
    * If the String has no namespace the Alfresco namespace is added.
    * If the String has a prefix an attempt to resolve the prefix to the
    * full URI will be made. 
    * 
    * @param str The string to convert
    * @return A QName representation of the given string 
    */
   public static QName resolveToQName(String str)
   {
      QName qname = null;
      
      if (str == null && str.length() == 0)
      {
         throw new IllegalArgumentException("str parameter is mandatory");
      }
      
      if (str.charAt(0) == (QName.NAMESPACE_BEGIN))
      {
         // create QName directly
         qname = QName.createQName(str);
      }
      else if (str.indexOf(QName.NAMESPACE_PREFIX) != -1)
      {
         // extract the prefix and try and resolve using the 
         // namespace service
         int end = str.indexOf(QName.NAMESPACE_PREFIX);
         String prefix = str.substring(0, end);
         String localName = str.substring(end + 1);
         NamespaceService nameSpcSvc = getNamespaceService();
         String uri = nameSpcSvc.getNamespaceURI(prefix);
         
         if (uri != null)
         {
            qname = QName.createQName(uri, localName);
         }
         else
         {
            logger.warn("Failed to resolve prefix " + prefix);
         }
      }
      else
      {
         // there's no namespace so prefix with Alfresco's
         qname = QName.createQName(NamespaceService.ALFRESCO_URI, str);
      }
      
      return qname;
   }
   
   /**
    * Creates a string representation of a QName for the given string.
    * If the given string already has a namespace, either a URL or a prefix,
    * nothing the given string is returned. If it does not have a namespace
    * the Alfresco namespace is added.
    * 
    * @param str The string to convert
    * @return A QName String representation of the given string 
    */
   public static String resolveToQNameString(String str)
   {
      String result = str;
      
      if (str == null && str.length() == 0)
      {
         throw new IllegalArgumentException("str parameter is mandatory");
      }
      
      if (str.charAt(0) != QName.NAMESPACE_BEGIN && str.indexOf(QName.NAMESPACE_PREFIX) != -1)
      {
         // get the prefix and resolve to the uri
         int end = str.indexOf(QName.NAMESPACE_PREFIX);
         String prefix = str.substring(0, end);
         String localName = str.substring(end + 1);
         NamespaceService nameSpcSvc = getNamespaceService();
         String uri = nameSpcSvc.getNamespaceURI(prefix);
         
         if (uri != null)
         {
            result = QName.NAMESPACE_BEGIN + uri + QName.NAMESPACE_END + localName;
         }
         else
         {
            logger.warn("Failed to resolve prefix " + prefix);
         }
      }
      else if (str.charAt(0) != QName.NAMESPACE_BEGIN)
      {
         // there's no namespace so prefix with Alfresco's
         result = QName.NAMESPACE_BEGIN + NamespaceService.ALFRESCO_URI + 
                  QName.NAMESPACE_END + str;
      }
      
      return result;
   }
   
   /**
    * Returns an instance of the namespace service
    * 
    * @return The NamespaceService
    */
   private static NamespaceService getNamespaceService()
   {
      if (namespaceService == null)
      {
         ServiceRegistry svcReg = getServiceRegistry(FacesContext.getCurrentInstance());
         namespaceService = svcReg.getNamespaceService();
      }
      
      return namespaceService;
   }
}
