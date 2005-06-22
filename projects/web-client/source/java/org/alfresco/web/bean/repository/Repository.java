package org.alfresco.web.bean.repository;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockStatus;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.repository.datatype.ValueConverter;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
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
   
   // TODO: TEMP! Replace this once we have "users" in the system!
   private static final String USERNAME = "admin";
   
   private static final String IMAGE_PREFIX = "/images/filetypes/";
   private static final String IMAGE_POSTFIX = ".gif";
   private static final String DEFAULT_FILE_IMAGE = IMAGE_PREFIX + "_default" + IMAGE_POSTFIX;
   private static final Map<String, String> s_fileExtensionMap = new HashMap<String, String>(89, 1.0f);
   
   /** cache of client StoreRef */
   private static StoreRef storeRef = null;
   
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
      storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, Application.getRepositoryStoreName(context));
      
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
         LockStatus lockStatus = lockService.getLockStatus(node.getNodeRef(), USERNAME);
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
    * 
    * @return the image path for the specified node type or the default icon if not found
    */
   public static String getFileTypeImage(Node node)
   {
      String image = DEFAULT_FILE_IMAGE;
      
      String name = node.getName();
      int extIndex = name.lastIndexOf('.');
      if (extIndex != -1 && name.length() > extIndex + 1)
      {
         String ext = name.substring(extIndex + 1).toLowerCase();
         
         // found file extension
         synchronized (s_fileExtensionMap)
         {
            image = s_fileExtensionMap.get(ext);
            if (image == null)
            {
               // not found create for first time
               image = IMAGE_PREFIX + ext + IMAGE_POSTFIX;
               
               // TODO: add support for Large filetype icons also!
               
               // does this image exist on the web-server?
               if (FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(image) != null)
               {
                  // found the image for this extension - save it for later
                  s_fileExtensionMap.put(ext, image);
               }
               else
               {
                  // not found, save the default image for this extension instead
                  image = DEFAULT_FILE_IMAGE;
                  s_fileExtensionMap.put(ext, image);
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
}
