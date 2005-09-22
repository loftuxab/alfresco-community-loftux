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
package org.alfresco.service.namespace;

import java.io.Serializable;
import java.util.Collection;

/**
 * <code>QName</code> represents the qualified name of a Repository item. Each
 * QName consists of a local name qualified by a namespace.
 * <p>
 * The {@link org.alfresco.service.namespace.QNamePattern QNamePattern} is implemented
 * to allow instances of this class to be used for direct pattern matching where
 * required on interfaces.
 * 
 * @author David Caruana
 * 
 */
public final class QName implements QNamePattern, Serializable, Cloneable
{
    private static final long serialVersionUID = 3977016258204348976L;

    private String namespaceURI;                // never null
    private String localName;                   // never null
    private int hashCode;
    private String prefix;
    private static final char[] INVALID_CHARS = { '/', '.', '{', '}' };
//    private static final char[] INVALID_CHARS = { '/', '.', '{', '}', ':' };

    public static final char NAMESPACE_PREFIX = ':';
    public static final char NAMESPACE_BEGIN = '{';
    public static final char NAMESPACE_END = '}';
    public static final int MAX_LENGTH = 100;

    
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
     * @param prefix  namespace prefix (maybe null or empty string)
     * @param localName  local name
     * @param prefixResolver  lookup to resolve mappings between prefix and namespace
     * @return  the QName
     */
    public static QName createQName(String prefix, String localName, NamespacePrefixResolver prefixResolver)
        throws InvalidQNameException, NamespaceException
    {
        // Validate Arguments
        if (localName == null || localName.length() == 0)
        {
            throw new InvalidQNameException("A QName must consist of a local name");
        }
        if (prefixResolver == null)
        {
            throw new IllegalArgumentException("A Prefix Resolver must be specified"); 
        }
        if (prefix == null)
        {
            prefix = NamespaceService.DEFAULT_PREFIX; 
        }
        
        // Calculate namespace URI and create QName
        String uri = prefixResolver.getNamespaceURI(prefix);
        if (uri == null)
        {
            throw new NamespaceException("Namespace prefix " + prefix + " is not mapped to a namespace URI");
        }
        return new QName(uri, localName, prefix);
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
        QName name = null;
        if (qname != null)
        {
            int colonIndex = qname.indexOf(NAMESPACE_PREFIX);
            String prefix = (colonIndex == -1) ? NamespaceService.DEFAULT_PREFIX : qname.substring(0, colonIndex);
            String localName = (colonIndex == -1) ? qname : qname.substring(colonIndex +1);
            name = createQName(prefix, localName, prefixResolver);
        }
        return name;
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
            throw new InvalidQNameException("QName '" + qname + "' must consist of a local name");
        }

        // Construct QName
        return new QName(namespaceURI, localName, null);
    }


    /**
     * Create a valid local name from the specified name
     * 
     * @param name  name to create valid local name from
     * @return valid local name
     */
    public static String createValidLocalName(String name)
    {
        // Validate length
        if (name == null || name.length() == 0)
        {
            throw new IllegalArgumentException("Local name cannot be null or empty.");
        }
        if (name.length() > MAX_LENGTH)
        {
            name = name.substring(0, MAX_LENGTH);
        }

        // make sure there are no spaces in the name (these are not considered
        // invalid
        // at the moment but things don't work if spaces are present), therefore
        // remove this if space becomes invalid or is dealt with
        name = name.replace(' ', '_');

        // search for any invalid QName characters
        for (int i = 0; i < INVALID_CHARS.length; i++)
        {
            // replace if found - slow but this is a rare case
            name = name.replace(INVALID_CHARS[i], '_');
        }

        return name;
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
        // Validate local name
        for (int i=0; i<INVALID_CHARS.length; i++)
        {
           if (name.indexOf(INVALID_CHARS[i]) != -1)
           {
               throw new InvalidQNameException("The local part of a QName may not contain '" + INVALID_CHARS[i] + "'");
           }
        }
        
        this.namespaceURI = (namespace == null) ? NamespaceService.DEFAULT_URI : namespace;
        this.prefix = prefix;
        this.localName = name;
        this.hashCode = 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
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
     * @return the namespace (empty string when not specified, but never null)
     */
    public String getNamespaceURI()
    {
        return this.namespaceURI;
    }


    /**
     * Gets a prefix resolved version of this QName
     * 
     * @param resolver  namespace prefix resolver
     * @return QName with prefix resolved
     */
    public QName getPrefixedQName(NamespacePrefixResolver resolver)
    {
        Collection<String> prefixes = resolver.getPrefixes(namespaceURI);
        if (prefixes.size() == 0)
        {
            throw new NamespaceException("A namespace prefix is not registered for uri " + namespaceURI);
        }
        String resolvedPrefix = prefixes.iterator().next();
        if (prefix != null && prefix.equals(resolvedPrefix))
        {
            return this;
        }
        return new QName(namespaceURI, localName, resolvedPrefix);        
    }
    
    
    /**
     * Two QNames are equal only when both their name and namespace match.
     * 
     * Note: The prefix is ignored during the comparison.
     */
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        else if (object == null)
        {
            return false;
        }
        if (object instanceof QName)
        {
            QName other = (QName) object;
            // namespaceURI and localname are not allowed to be null
            return (this.namespaceURI.equals(other.namespaceURI) &&
                    this.localName.equals(other.localName));
        }
        else
        {
            return false;
        }
    }

    /**
     * Performs a direct comparison between qnames.
     * 
     * @see #equals(Object)
     */
    public boolean isMatch(QName qname)
    {
        return this.equals(qname);
    }

    /**
     * Calculate hashCode. Follows pattern used by String where hashCode is
     * cached (QName is immutable).
     */
    public int hashCode()
    {
        if (this.hashCode == 0)
        {
            // the hashcode assignment is atomic - it is only an integer
            this.hashCode = ((37 * localName.hashCode()) + namespaceURI.hashCode());
        }
        return this.hashCode;
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
     * Render string representation of QName using format:
     * 
     * <code>prefix:name</code>
     * 
     * @return the string representation
     */
    public String toPrefixString()
    {
        return (prefix == null) ? localName : prefix + NAMESPACE_PREFIX + localName;
    }


    /**
     * Render string representation of QName using format:
     * 
     * <code>prefix:name</code>
     * 
     * according to namespace prefix mappings of specified namespace resolver.
     * 
     * @param prefixResolver namespace prefix resolver
     * 
     * @return  the string representation
     */
    public String toPrefixString(NamespacePrefixResolver prefixResolver)
    {
        Collection<String> prefixes = prefixResolver.getPrefixes(namespaceURI);
        if (prefixes.size() == 0)
        {
            throw new NamespaceException("A namespace prefix is not registered for uri " + namespaceURI);
        }
        String prefix = prefixes.iterator().next();
        if (prefix.equals(NamespaceService.DEFAULT_PREFIX))
        {
            return localName;
        }
        else
        {
            return prefix + NAMESPACE_PREFIX + localName;
        }
    }
    
}
