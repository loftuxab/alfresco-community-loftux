package org.alfresco.repo.ref;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Representation of a simple path e.g.
 * <b><pre>
 *   /x/y/z
 * </pre></b>
 * In the above example, there will be <b>4</b> elements, the first being a reference
 * to the root node, followed by qname elements for <b>x</b>, <b>x</b> and <b>z</b>.    
 * <p>
 * Methods and constructors are available to construct a <code>Path</code> instance
 * from a path string or by building the path incrementally, including the ability to
 * append and prepend path elements.
 * <p>
 * Path elements supported:
 * <ul>
 *   <li><b>/{namespace}name</b> fully qualified element</li>
 *   <li><b>/name</b> element using default namespace</li>
 *   <li><b>/{namespace}name[n]</b> nth sibling</li>
 *   <li><b>/name[n]</b> nth sibling using default namespace</li>
 *   <li><b>/descendant-or-self::node()</b> descendent or self</li>
 *   <li><b>/.</b> self</li>
 *   <li><b>/..</b> parent</li>
 * </ul>
 * 
 * @author Derek Hulley
 */
public final class Path implements Iterable<Path.Element>
{
    private LinkedList<Element> elements;
    
    public Path()
    {
        // use linked list so as random access is not required, but both prepending and appending is
        elements = new LinkedList<Element>();
    }
    
    /**
     * Get a typed iterator over the path elements.
     * 
     * @return
     */
    
    public Iterator<Path.Element> iterator()
    {
       return elements.iterator();
    }
    
    /**
     * Add a path element to the beginning of the path.  This operation is useful in cases where
     * a path is built by traversing up a hierarchy.
     * 
     * @param pathElement
     * @return Returns this instance of the path
     */
    public Path prepend(Path.Element pathElement)
    {
        elements.addFirst(pathElement);
        return this;
    }
    
    /**
     * Merge the given path into the beginning of this path.
     * 
     * @param path
     * @return Returns this instance of the path
     */
    public Path prepend(Path path)
    {
       elements.addAll(0, path.elements);
       return this;
    }
    
    /**
     * Appends a path element to the end of the path
     * 
     * @param pathElement
     * @return Returns this instance of the path
     */
    public Path append(Path.Element pathElement)
    {
        elements.addLast(pathElement);
        return this;
    }
    
    /**
     * Append the given path of this path.
     * 
     * @param path
     * @return Returns this instance of the path
     */
    public Path append(Path path)
    {
       elements.addAll(path.elements);
       return this;
    }
    
    /**
     * @return Returns the first element in the path or null if the path is empty
     */
    public Element first()
    {
        return elements.getFirst();
    }
    
    /**
     * @return Returns the last element in the path or null if the path is empty
     */
    public Element last()
    {
        return elements.getLast();
    }
    
    /**
     * @return Returns a string path made up of the component elements of this instance
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder(128);
        for (Element element : elements)
        {
            sb.append(element.getElementString());
        }
        return sb.toString();
    }
    
    /**
     * Represents a path element.
     * <p>
     * In <b>/x/y/z</b>, elements are <b>x</b>, <b>y</b> and <b>z</b>.
     */
    public abstract static class Element
    {
        /**
         * @return Returns the path element portion including leading '/' and never null
         */
        public abstract String getElementString();
        
        /**
         * @see #getElementString()
         */
        public String toString()
        {
            return getElementString();
        }
    }
    
    /**
     * Represents a qualified path between a parent and a child node,
     * including the sibling to retrieve e.g. <b>/{namespace}name[5]</b> 
     */
    public static class ChildAssocElement extends Element
    {
        private ChildAssocRef ref;
        
        /**
         * @param ref a reference to the specific parent-child association
         */
        public ChildAssocElement(ChildAssocRef ref)
        {
            this.ref = ref;
        }
        public String getElementString()
        {
            StringBuilder sb = new StringBuilder(32);
            if (ref.getParentRef() == null)
            {
                // there is no parent, i.e. it is a reference to a root node
            }
            else
            {
                // a parent is present
                sb.append("/").append(ref.getQName());
            }
            if (ref.getNthSibling() > -1)
            {
                sb.append("[").append(ref.getNthSibling()).append("]");
            }
            return sb.toString();
        }
        public ChildAssocRef getRef()
        {
            return ref;
        }
    }

    /**
     * Represents the <b>//</b> or <b>/descendant-or-self::node()</b> xpath element
     */
    public static class DescendentOrSelfElement extends Element
    {
        public String getElementString()
        {
            return "/descendant-or-self::node()";
        }
    }
    
    /**
     * Represents the <b>/.</b> xpath element
     */
    public static class SelfElement extends Element
    {
        public String getElementString()
        {
            return "/.";
        }
    }
    
    /**
     * Represents the <b>/..</b> xpath element
     */
    public static class ParentElement extends Element
    {
        public String getElementString()
        {
            return "/..";
        }
    }
}
