package com.activiti.repo.ref;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Representation of a simple path e.g.
 * <b><pre>
 *   /x/y/z
 * </pre></b>
 * Methods and constructors are available to construct a <code>Path</code> instance
 * from a path string or by building the path incrementally, including the ability to
 * append and prepend path elements.
 * <p>
 * Path elements supported:
 * <ul>
 *   <li><b>//</b> descendent or self</li>
 *   <li><b>/.</b> self</li>
 *   <li><b>/..</b> parent</li>
 *   <li><b>/{namespace}name</b> fully qualified element</li>
 *   <li><b>/name</b> element using default namespace</li>
 *   <li><b>/{namespace}name[n]</b> nth sibling</li>
 *   <li><b>/name[n]</b> nth sibling using default namespace</li>
 * </ul>
 * 
 * @author Derek Hulley
 */
public class Path
{
    private LinkedList<Element> elements;
    
    public Path()
    {
        // use linked list so as random access is not required, but both prepending and appending is
        elements = new LinkedList<Element>();
    }
    
    /**
     * This is required as we cannot use the <code>Iterable</code> iterface directly.
     * 
     * @return Returns a collection of <code>Path.Element</code> instances that can be iterated over
     */
    public Collection<Element> getElements()
    {
        return elements;
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
     * @return Returns a string path made up of the component elements of this instance
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer(50);
        for (Element element : elements)
        {
            sb.append("/").append(element.getElementString());
        }
        return sb.toString();
    }
    
    /**
     * Represents a path element.
     * <p>
     * In <b>x/y/z</b>, elements are <b>x</b>, <b>y</b> and <b>z</b>.
     */
    public abstract static class Element
    {
        /**
         * @return Returns the path element portion excluding leading '/' and never null
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
            StringBuffer sb = new StringBuffer(ref.getName().toString());
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
     * Represents the <b>//</b> xpath element
     */
    public static class DescendentOrSelfElement extends Element
    {
        public String getElementString()
        {
            return "";
        }
    }
    
    /**
     * Represents the <b>/.</b> xpath element
     */
    public static class SelfElement extends Element
    {
        public String getElementString()
        {
            return ".";
        }
    }
    
    /**
     * Represents the <b>/..</b> xpath element
     */
    public static class ParentElement extends Element
    {
        public String getElementString()
        {
            return "..";
        }
    }
}
