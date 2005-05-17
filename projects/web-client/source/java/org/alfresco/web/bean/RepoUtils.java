/*
 * Created on 17-May-2005
 */
package org.alfresco.web.bean;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.dictionary.NamespaceService;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.repo.search.ResultSetRow;
import org.alfresco.repo.value.ValueConverter;

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
   
   
   private static final QName QNAME_NAME = QName.createQName(NamespaceService.ALFRESCO_URI, "name");
}
