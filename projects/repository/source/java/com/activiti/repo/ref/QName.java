package com.activiti.repo.ref;

import java.io.Serializable;


/**
 * <code>QName</code> represents the qualified name of a Repository item.
 * Each QName consists of a local name qualified by a namespace.
 * 
 * @author David Caruana
 * 
 */
public final class QName
   implements Serializable
{
   
   private static final long serialVersionUID = 246716213529037024L;
   
   private String prefix;
   private String namespace;
   private String name;
   private int hashCode;
   
   private static char NAMESPACE_BEGIN = '{';
   private static char NAMESPACE_END = '}';
   

   /**
    * Create a QName
    * 
    * @param namespace  the qualifying namespace (maybe null or empty string)
    * @param name  the qualified name
    * @return  the QName
    */
   public static QName createQName(String namespace, String name)
   {
      if (name == null || name.length() == 0)
      {
         throw new InvalidQNameException("A QName must consist of a local name");
      }
      return new QName(namespace, name, null);
   }
   

   /**
    * Create a QName from its string representation of the following
    * format:
    * 
    * <code>{namespace}name</code>
    * 
    * @param qname  the string representation of the QName
    * @return  the QName
    * @throws IllegalArgumentException
    * @throws InvalidQNameException
    */
   public static QName createQName(String qname)
      throws InvalidQNameException
   {
      if (qname == null || qname.length() == 0)
      {
         throw new InvalidQNameException("Argument qname is mandatory");
      }
      
      String namespace = null;
      String name = null;
      
      // Parse namespace
      int namespaceBegin = qname.indexOf(NAMESPACE_BEGIN);
      int namespaceEnd = -1;
      if (namespaceBegin != -1)
      {
         if (namespaceBegin != 0)
         {
            throw new InvalidQNameException("QName '" + qname + "' must start with a namespace");
         }
         namespaceEnd = qname.indexOf(NAMESPACE_END, namespaceBegin +1);
         if (namespaceEnd == -1)
         {
            throw new InvalidQNameException("QName '" + qname + "' is missing the closing namespace " + NAMESPACE_END + " token");
         }
         namespace = qname.substring(namespaceBegin +1, namespaceEnd);
      }
      
      // Parse name
      name = qname.substring(namespaceEnd +1);
      if (name == null || name.length() == 0)
      {
         throw new InvalidQNameException("QName '" + qname + "' must specify a local name");
      }
      
      // Construct QName
      return new QName(namespace, name, null);
   }

   
   // TODO: Implement this...
   // public static QName createQName(String prefix, NamespaceMap namespaceMap, String name)


   /**
    * Gets the name
    * 
    * @return  the name
    */
   public String getName()
   {
     return this.name;
   }
   
   
   /**
    * Gets the namespace
    * 
    * @return  the namespace (empty string when not specified)
    */
   public String getNamespace()
   {   
     return this.namespace;
   }
   

   /**
    * Two QNames are equal only when both their name and namespace match.
    * 
    * Note: The prefix is ignored during the comparison.
    */
   public boolean equals(Object object)
   {
      if (object instanceof QName)
      {
         QName other = (QName)object;
         if (this.namespace.equals(other.namespace) && this.name.equals(other.name))
         {
            return true;
         }
      }
      return false;
   }


   /**
    * Calculate hashCode.  Follows pattern used by String where hashCode is
    * cached (QName is immutable). 
    */
   public int hashCode() 
   {
      int h = this.hashCode;
      if (h == 0)
      {
         h = name.hashCode();
         h = 37 * h + namespace.hashCode();
         this.hashCode = h;
      }
      return h;
   }


   /**
    * Render string representation of QName using format:
    * 
    * <code>{namespace}name</code>
    * 
    * @return the string representation
    */
   public String toString()
   {
      return NAMESPACE_BEGIN + namespace + NAMESPACE_END + name;
   }

   
   /**
    * Construct QName
    * 
    * @param namespace  qualifying namespace (maybe null or empty string)
    * @param name  qualified name
    * @param prefix  prefix (maybe null or empty string)
    */
   private QName(String namespace, String name, String prefix)
   {
      this.namespace = (namespace == null) ? "" : namespace;
      this.name = name;
      this.prefix = prefix;
      this.hashCode = 0;
   }


   /**
    * Default Constructor
    */
   private QName()
   {
   }

} 
