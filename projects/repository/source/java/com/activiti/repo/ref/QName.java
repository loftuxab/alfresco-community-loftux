package com.activiti.repo.ref;

import java.io.Serializable;

/**
 * <code>QName</code> represents the qualified name of a Repository item. Each
 * QName consists of a local name qualified by a namespace.
 * 
 * @author David Caruana
 * 
 */
public final class QName implements Serializable
{

    private static final long serialVersionUID = 246716213529037024L;

    private String prefix;
    private String namespaceURI;
    private String localName;
    private int hashCode;

    private static char NAMESPACE_BEGIN = '{';
    private static char NAMESPACE_END = '}';


    /**
     * Create a QName
     * 
     * @param namespaceURI  the qualifying namespace (maybe null or empty string)
     * @param localName  the qualified name
     * @return the QName
     */
    public static QName createQName(String namespaceURI, String localName)
        throws InvalidQNameException
    {
        if (localName == null || localName.length() == 0)
        {
            throw new InvalidQNameException("A QName must consist of a local name");
        }
        return new QName(namespaceURI, localName, null);
    }


    /**
     * Create a QName
     * 
     * @param prefix  namespace prefix
     * @param localName  local name
     * @param prefixResolver  lookup to resolve mappings between prefix and namespace
     * @return  the QName
     */
    public static QName createQName(String prefix, String localName, NamespacePrefixResolver prefixResolver)
        throws InvalidQNameException, NamespaceException
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Create a QName
     * 
     * @param qname  qualified name of the following format <code>prefix:localName</code>
     * @param prefixResolver  lookup to resolve mappings between prefix and namespace
     * @return  the QName
     */
    public static QName createQName(String qname, NamespacePrefixResolver prefixResolver)
        throws InvalidQNameException, NamespaceException
    {
        throw new UnsupportedOperationException();
    }


    /**
     * Create a QName from its internal string representation of the following format:
     * 
     * <code>{namespaceURI}localName</code>
     * 
     * @param qname  the string representation of the QName
     * @return the QName
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

        String namespaceURI = null;
        String localName = null;

        // Parse namespace
        int namespaceBegin = qname.indexOf(NAMESPACE_BEGIN);
        int namespaceEnd = -1;
        if (namespaceBegin != -1)
        {
            if (namespaceBegin != 0)
            {
                throw new InvalidQNameException("QName '" + qname + "' must start with a namespaceURI");
            }
            namespaceEnd = qname.indexOf(NAMESPACE_END, namespaceBegin + 1);
            if (namespaceEnd == -1)
            {
                throw new InvalidQNameException("QName '" + qname + "' is missing the closing namespace " + NAMESPACE_END + " token");
            }
            namespaceURI = qname.substring(namespaceBegin + 1, namespaceEnd);
        }

        // Parse name
        localName = qname.substring(namespaceEnd + 1);
        if (localName == null || localName.length() == 0)
        {
            throw new InvalidQNameException("QName '" + qname + "' must specify a local name");
        }

        // Construct QName
        return new QName(namespaceURI, localName, null);
    }


    /**
     * Gets the name
     * 
     * @return the name
     */
    public String getLocalName()
    {
        return this.localName;
    }


    /**
     * Gets the namespace
     * 
     * @return the namespace (empty string when not specified)
     */
    public String getNamespaceURI()
    {
        return this.namespaceURI;
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
            QName other = (QName) object;
            if (this.namespaceURI.equals(other.namespaceURI) && this.localName.equals(other.localName))
            {
                return true;
            }
        }
        return false;
    }


    /**
     * Calculate hashCode. Follows pattern used by String where hashCode is
     * cached (QName is immutable).
     */
    public int hashCode()
    {
        int h = this.hashCode;
        if (h == 0)
        {
            h = localName.hashCode();
            h = 37 * h + namespaceURI.hashCode();
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
        return NAMESPACE_BEGIN + namespaceURI + NAMESPACE_END + localName;
    }


    /**
     * Construct QName
     * 
     * @param namespace
     *            qualifying namespace (maybe null or empty string)
     * @param name
     *            qualified name
     * @param prefix
     *            prefix (maybe null or empty string)
     */
    private QName(String namespace, String name, String prefix)
    {
        this.namespaceURI = (namespace == null) ? "" : namespace;
        this.localName = name;
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
