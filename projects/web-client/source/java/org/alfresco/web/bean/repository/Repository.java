/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
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
import org.alfresco.repo.configuration.ConfigurableService;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.metadata.MetadataExtracter;
import org.alfresco.repo.content.metadata.MetadataExtracterRegistry;
import org.alfresco.repo.security.authentication.RepositoryAuthenticationDao;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.web.app.Application;
import org.alfresco.web.ui.common.Utils;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Helper class for accessing repository objects, convert values, escape values and filetype icons.
 * 
 * @author gavinc
 * @author kevinr
 */
public final class Repository
{
   /** I18N error messages */
   public static final String ERROR_NODEREF = "error_noderef";
   public static final String ERROR_GENERIC = "error_generic";
   public static final String ERROR_NOHOME  = "error_homespace";
   public static final String ERROR_SEARCH  = "error_search";
   
   private static final String IMAGE_PREFIX16 = "/images/filetypes/";
   private static final String IMAGE_PREFIX32 = "/images/filetypes32/";
   private static final String IMAGE_POSTFIX = ".gif";
   private static final String DEFAULT_FILE_IMAGE16 = IMAGE_PREFIX16 + "_default" + IMAGE_POSTFIX;
   private static final String DEFAULT_FILE_IMAGE32 = IMAGE_PREFIX32 + "_default" + IMAGE_POSTFIX;
   
   private static final String METADATA_EXTACTER_REGISTRY = "metadataExtracterRegistry";  

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
      storeRef = Application.getRepositoryStoreRef(context);
      
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
      String name = null;
      
      // try to find a display "name" property for this node
      Object nameProp = nodeService.getProperty(ref, ContentModel.PROP_NAME);
      if (nameProp != null)
      {
         name = nameProp.toString();
      }
      else
      {
         // revert to using QName if not found
         QName qname = nodeService.getPrimaryParent(ref).getQName();
         if (qname != null)
         {
            name = qname.getLocalName();
         }
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
    * Return whether a Node is currently locked
    * 
    * @param node             The Node wrapper to test against
    * @param lockService      The LockService to use
    * 
    * @return whether a Node is currently locked
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
    * Return whether a Node is currently locked by the current user
    * 
    * @param node             The Node wrapper to test against
    * @param lockService      The LockService to use
    * 
    * @return whether a Node is currently locked by the current user
    */
   public static Boolean isNodeOwnerLocked(Node node, LockService lockService)
   {
      Boolean locked = Boolean.FALSE;
      
      if (node.hasAspect(ContentModel.ASPECT_LOCKABLE) &&
          lockService.getLockStatus(node.getNodeRef()) == LockStatus.LOCK_OWNER)
      {
         locked = Boolean.TRUE;
      }
      
      return locked;
   }
   
   /**
    * Return whether a WorkingCopy Node is owned by the current User
    * 
    * @param node             The Node wrapper to test against
    * @param lockService      The LockService to use
    * 
    * @return whether a WorkingCopy Node is owned by the current User
    */
   public static Boolean isNodeOwner(Node node, LockService lockService)
   {
      Boolean locked = Boolean.FALSE;
      
      if (node.hasAspect(ContentModel.ASPECT_WORKING_COPY))
      {
         Object obj = node.getProperties().get("workingCopyOwner");
         if (obj instanceof String)
         {
            User user = Application.getCurrentUser(FacesContext.getCurrentInstance());
            if ( ((String)obj).equals(user.getUserName()))
            {
               locked = Boolean.TRUE;
            }
         }
      }
      
      return locked;
   }
   
   /**
    * Return the human readable form of the specified node Path. Fast version of the method that
    * simply converts QName localname components to Strings.
    * 
    * @param path    Path to extract readable form from, excluding the final element
    * 
    * @return human readable form of the Path excluding the final element
    */
   public static String getDisplayPath(Path path)
   {
      StringBuilder buf = new StringBuilder(64);
      
      for (int i=0; i<path.size()-1; i++)
      {
         String elementString = null;
         Path.Element element = path.get(i);
         if (element instanceof Path.ChildAssocElement)
         {
            ChildAssociationRef elementRef = ((Path.ChildAssocElement)element).getRef();
            if (elementRef.getParentRef() != null)
            {
               elementString = elementRef.getQName().getLocalName().replace('_', ' ');
            }
         }
         else
         {
            elementString = element.getElementString();
         }
         
         if (elementString != null)
         {
            buf.append("/");
            buf.append(elementString);
         }
      }
      
      return buf.toString();
   }
   
   /**
    * Return the human readable form of the specified node Path. Slow version of the method
    * that extracts the name of each node in the Path from the supplied NodeService.
    * 
    * @param path    Path to extract readable form from, excluding the final element
    * 
    * @return human readable form of the Path excluding the final element
    */
   public static String getDisplayPath(Path path, NodeService nodeService)
   {
      StringBuilder buf = new StringBuilder(64);
      
      for (int i=0; i<path.size()-1; i++)
      {
         String elementString = null;
         Path.Element element = path.get(i);
         if (element instanceof Path.ChildAssocElement)
         {
            ChildAssociationRef elementRef = ((Path.ChildAssocElement)element).getRef();
            if (elementRef.getParentRef() != null)
            {
               elementString = Repository.getNameForNode(nodeService, elementRef.getChildRef());
               Serializable nameProp = nodeService.getProperty(elementRef.getChildRef(), ContentModel.PROP_NAME);
               if (nameProp != null)
               {
                  // use the name property if we find it
                  elementString = nameProp.toString();
               }
               else
               {
                  // revert to using QName replacement if not found
                  elementString = elementRef.getQName().getLocalName().replace('_', ' ');
               }
            }
         }
         else
         {
            elementString = element.getElementString();
         }
         
         if (elementString != null)
         {
            buf.append("/");
            buf.append(elementString);
         }
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
      String mimetype = MimetypeMap.MIMETYPE_BINARY;
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
       TransactionService transactionService = getServiceRegistry(context).getTransactionService(); 
       return transactionService.getUserTransaction();
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
    * Return the Repository Service Registry
    * 
    * @param context Servlet Context
    * @return the Service Registry
    */
   public static ServiceRegistry getServiceRegistry(ServletContext context)
   {
       return (ServiceRegistry)WebApplicationContextUtils.getRequiredWebApplicationContext(
               context).getBean(ServiceRegistry.SERVICE_REGISTRY);
   }

   /**
    * Return the Configurable Service
    * 
    * @return the configurable service
    */
   public static ConfigurableService getConfigurableService(FacesContext context)
   {
       return (ConfigurableService)FacesContextUtils.getRequiredWebApplicationContext(context).getBean("configurableService");
   }

   /**
    * Return the Metadata Extracter Registry
    * 
    * @param context Faces Context
    * @return the MetadataExtracterRegistry
    */
   public static MetadataExtracterRegistry getMetadataExtracterRegistry(FacesContext context)
   {
       return (MetadataExtracterRegistry)FacesContextUtils.getRequiredWebApplicationContext(
               context).getBean(METADATA_EXTACTER_REGISTRY);
   }
   
   /**
    * Extracts the metadata of a "raw" piece of content into a map. 
    * 
    * @param context Faces Context
    * @param reader Content reader for the source content to extract from 
    * @param destination Map of metadata to set metadata values into
    * @return True if an extracter was found
    */
   public static boolean extractMetadata(FacesContext context, ContentReader reader, Map<QName, Serializable> destination)
   {
      // check that source mimetype is available
      String mimetype = reader.getMimetype();
      if (mimetype == null)
      {
         throw new AlfrescoRuntimeException("The content reader mimetype must be set: " + reader);
      }

      // look for a transformer
      MetadataExtracter extracter = getMetadataExtracterRegistry(context).getExtracter(mimetype);
      if (extracter == null)
      {
         // No metadata extracter is not a failure, but we flag it 
         return false;
      }
      
      // we have a transformer, so do it
      extracter.extract(reader, destination);
      return true;
   }

   /**
    * Query a list of Person type nodes from the repo
    * It is currently assumed that all Person nodes exist below the Repository root node
    * 
    * @param context Faces Context
    * @param nodeService The node service
    * @param searchService used to perform the search
    * @return List of Person node objects
    */
   public static List<Node> getUsers(FacesContext context, NodeService nodeService, SearchService searchService)
   {
      List<Node> personNodes = null;
      
      UserTransaction tx = null;
      try
      {
         tx = Repository.getUserTransaction(context);
         tx.begin();
         
         PersonService personService = (PersonService)FacesContextUtils.getRequiredWebApplicationContext(context).getBean("personService");
         NodeRef peopleRef = personService.getPeopleContainer();
         
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
               NodeRef homeFolderNodeRef = (NodeRef)props.get("homeFolder");
               if (homeFolderNodeRef != null)
               {
                  props.put("homeSpace", homeFolderNodeRef);
               }
               
               personNodes.add(node);
            }
         }
         
         // commit the transaction
         tx.commit();
      }
      catch (InvalidNodeRefException refErr)
      {
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               context, Repository.ERROR_NODEREF), new Object[] {"root"}) );
         personNodes = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      catch (Exception err)
      {
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               context, Repository.ERROR_GENERIC), err.getMessage()), err );
         personNodes = Collections.<Node>emptyList();
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
      }
      
      return personNodes;
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
         // there's no namespace so prefix with Alfresco's Content Model
         qname = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, str);
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
         // there's no namespace so prefix with Alfresco's Content Model
         result = QName.NAMESPACE_BEGIN + NamespaceService.CONTENT_MODEL_1_0_URI + 
                  QName.NAMESPACE_END + str;
      }
      
      return result;
   }
   
   /**
    * Convert a property of unknown type to a String value. A native String value will be
    * returned directly, else toString() will be executed, null is returned as null. 
    * 
    * @param value      Property value
    * 
    * @return value to String or null
    */
   public static String safePropertyToString(Serializable value)
   {
      if (value == null)
      {
         return null;
      }
      else if (value instanceof String)
      {
         return (String)value;
      }
      else
      {
         return value.toString();
      }
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
