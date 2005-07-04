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
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.ui.common.Utils;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Helper class for accessing the repository
 * 
 * @author gavinc
 */
public final class Repository
{
   public static final String ERROR_NODEREF = "Unable to find the repository node referenced by Id: {0} - the node has probably been deleted from the database.";
   public static final String ERROR_GENERIC = "A system error occured during the operation: {0}";
   public static final String ERROR_NOHOME  = "The Home Space node referenced by Id: {0} cannot be found. It may have been deleted from the database. Please contact your system administrator.";
   public static final String ERROR_SEARCH  = "Search failed due to system error: {0}";
   
   private static final String IMAGE_PREFIX16 = "/images/filetypes/";
   private static final String IMAGE_PREFIX32 = "/images/filetypes32/";
   private static final String IMAGE_POSTFIX = ".gif";
   private static final String DEFAULT_FILE_IMAGE16 = IMAGE_PREFIX16 + "_default" + IMAGE_POSTFIX;
   private static final String DEFAULT_FILE_IMAGE32 = IMAGE_PREFIX32 + "_default" + IMAGE_POSTFIX;
   private static final Map<String, String> s_fileExtensionMap = new HashMap<String, String>(89, 1.0f);
   
   /** cache of client StoreRef */
   private static StoreRef storeRef = null;
   
   /** reference to person folder */
   private static NodeRef peopleRef = null;
   
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
    * Return the string value of a QName property based on the supplied property name.
    * <p>
    * If convertNull is set, the method will convert null values to the empty string.
    * 
    * @param props            Property map to retrieve value from
    * @param property         The name of the property to retrieve via QName 
    * @param convertNull      Whether to convert null values to the empty string
    * 
    * @return property as string value
    */
   public static String getQNameProperty(Map<QName, Serializable> props, String property, boolean convertNull)
   {
      String value = null;
      
      QName propQName = QName.createQName(NamespaceService.ALFRESCO_URI, property);
      Object obj = props.get(propQName);
      
      if (obj != null)
      {
         value = obj.toString();
      }
      else if (convertNull == true && obj == null)
      {
         value = "";
      }
      
      return value;
   }

   /**
    * Return the string value of a QName property based on the supplied property name.
    * <p>
    * If convertNull is set, the method will convert null values to the empty string.
    * 
    * @param row              ResultSetRow to retrieve value from
    * @param property         The name of the property to retrieve via QName 
    * @param convertNull      Whether to convert null values to the empty string
    * 
    * @return property as string value
    */
   public static String getValueProperty(ResultSetRow row, String name, boolean convertNull)
   {
      Serializable value = row.getValue(QName.createQName(NamespaceService.ALFRESCO_URI, name));
      String property = null;
      if (value != null)
      {
         property = ValueConverter.convert(String.class, value);
      }
      
      if (convertNull == true && property == null)
      {
         property = "";
      }
      
      return property;
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
         // TODO: replace username with real user name ref here!
         LockStatus lockStatus = lockService.getLockStatus(node.getNodeRef());
         if (lockStatus == LockStatus.LOCKED || lockStatus == LockStatus.LOCK_OWNER)
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
               
               // TODO: add support for Large filetype icons also!
               
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
    * @return mimetype for the specified filename - falls back to 'text/plain' if not found.
    */
   public static String getMimeTypeForFileName(FacesContext context, String filename)
   {
      // base the mimetype from the file extension
      MimetypeService mimetypeService = (MimetypeService)getServiceRegistry(context).getMimetypeService();
      
      // fall back if mimetype not found
      String mimetype = "text/plain";
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
         // get a reference to the system types folder node
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
            throw new AlfrescoRuntimeException("Unable to find system types folder: " + RepositoryAuthenticationDao.PEOPLE_FOLDER);
         }
         
         peopleRef = results.get(0);
      }
      
      return peopleRef;
   }
}
