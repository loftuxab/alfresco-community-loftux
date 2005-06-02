/*
 * Created on 17-May-2005
 */
package org.alfresco.web.bean;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.dictionary.impl.DictionaryBootstrap;
import org.alfresco.repo.lock.LockService;
import org.alfresco.repo.lock.LockStatus;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.ResultSetRow;
import org.alfresco.repo.value.ValueConverter;
import org.alfresco.web.bean.repository.Node;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * @author Kevin Roast
 */
public final class RepoUtils
{
   /**
    * Private Constructor
    */
   private RepoUtils()
   {
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
      Object nameProp = nodeService.getProperty(ref, QNAME_NAME);
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
    * @param ref              NodeRef to test
    * 
    * @return whether a Node is current Locked
    */
   public static Boolean isNodeLocked(Node node, LockService lockService, NodeRef ref)
   {
      Boolean locked = Boolean.FALSE;
      if (node.hasAspect(DictionaryBootstrap.ASPECT_QNAME_LOCK))
      {
         // TODO: replace username with real user name ref here!
         LockStatus lockStatus = lockService.getLockStatus(ref, USERNAME);
         if (lockStatus == LockStatus.LOCKED || lockStatus == LockStatus.LOCK_OWNER)
         {
            locked = Boolean.TRUE;
         }
      }
      
      return locked;
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
      MimetypeMap mimetypeMap = (MimetypeMap)FacesContextUtils.getRequiredWebApplicationContext(context).getBean("mimetypeMap");
      
      // fall back if mimetype not found
      String mimetype = "text/plain";
      int extIndex = filename.lastIndexOf('.');
      if (extIndex != -1)
      {
         String mt = mimetypeMap.getMimetypesByExtension().get(extIndex);
         if (mt != null)
         {
            mimetype = mt;
         }
      }
      
      return mimetype;
   }
   
   
   public static final QName QNAME_NAME = QName.createQName(NamespaceService.ALFRESCO_URI, "name");
   
   // TODO: TEMP! Replace this once we have "users" in the system!
   private static final String USERNAME = "admin";
   
   public static final String ERROR_NODEREF = "Unable to find the repository node referenced by Id: {0} - the node has probably been deleted from the database.";
   
   private final static String IMAGE_PREFIX = "/images/filetypes/";
   private final static String IMAGE_POSTFIX = ".gif";
   private final static String DEFAULT_FILE_IMAGE = IMAGE_PREFIX + "_default" + IMAGE_POSTFIX;
   private final static Map<String, String> s_fileExtensionMap = new HashMap<String, String>(89, 1.0f);
}
