package org.alfresco.repo.ref;

import junit.framework.TestCase;

/**
 * @see org.alfresco.repo.ref.Path
 * 
 * @author Derek Hulley
 */
public class PathTest extends TestCase
{
    private Path path;
    private QName qname;
    private StoreRef storeRef;
    private NodeRef parentRef;
    private NodeRef childRef;
    
    public PathTest(String name)
    {
        super(name);
    }
    
    public void setUp() throws Exception
    {
        super.setUp();
        path = new Path();
        qname = QName.createQName("http://www.google.com", "documentx");
        storeRef = new StoreRef("x", "y");
        parentRef = new NodeRef(storeRef, "P");
        childRef = new NodeRef(storeRef, "C");
    }
    
    public void testQNameElement() throws Exception
    {
        // plain
        Path.Element element = new Path.ChildAssocElement(new ChildAssocRef(parentRef, qname, childRef));
        assertEquals("Element string incorrect",
                "/" + qname.toString(),
                element.getElementString());
        // sibling
        element = new Path.ChildAssocElement(new ChildAssocRef(parentRef, qname, childRef, true, 5));
        assertEquals("Element string incorrect", "/{http://www.google.com}documentx[5]", element.getElementString());
    }
    
    public void testElementTypes() throws Exception
    {
        Path.Element element = new Path.DescendentOrSelfElement();
        assertEquals("DescendentOrSelf element incorrect",
                "/descendant-or-self::node()",
                element.getElementString());
        
        element = new Path.ParentElement();
        assertEquals("Parent element incorrect", "/..", element.getElementString());
        
        element = new Path.SelfElement();
        assertEquals("Self element incorrect", "/.", element.getElementString());
    }
    
    public void testAppendingAndPrepending() throws Exception
    {
        Path.Element element1 = new Path.ChildAssocElement(new ChildAssocRef(parentRef, qname, childRef, true, 4));
        Path.Element element2 = new Path.DescendentOrSelfElement();
        Path.Element element3 = new Path.ParentElement();
        Path.Element element4 = new Path.SelfElement();
        // append them all to the path
        path.append(element1).append(element2).append(element3).append(element4);
        // check
        assertEquals("Path appending didn't work",
                "/{http://www.google.com}documentx[4]/descendant-or-self::node()/../.",
                path.toString());
        
        // copy the path
        Path copy = new Path();
        copy.append(path).append(path);
        // check
        assertEquals("Path appending didn't work",
                path.toString() + path.toString(),
                copy.toString());
        
        // prepend
        path.prepend(element2);
        // check
        assertEquals("Prepending didn't work",
                "/descendant-or-self::node()/{http://www.google.com}documentx[4]/descendant-or-self::node()/../.",
                path.toString());
    }
}