/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.module.phpIntegration.lib;


/**
 * Child association object.
 * 
 * @author Roy Wetherall
 */
public class ChildAssociation implements ScriptObject
{
    /** The name of the script extension */
    private static final String SCRIPT_OBJECT_NAME = "ChildAssociation";
    
    /** The parent node */
    private Node parent;
    
    /** The child node */
    private Node child;
    
    /** The type of the child association */
    private String type;
    
    /** The name of the child association */
    private String name;
    
    /** Indicates whether the child association is primary or not */
    private boolean isPrimary;
    
    /** The sibling order */
    private int nthSibling;
    
    /**
     * Constructor
     * 
     * @param parent        the parent node
     * @param child         the child node
     * @param type          the association type
     * @param name          the association name
     * @param isPrimary     indicates whether the association is primary or not
     * @param nthSibling    the sibling oreder
     */
    public ChildAssociation(Node parent, Node child, String type, String name, boolean isPrimary, int nthSibling)
    {
        this.parent = parent;
        this.child = child;
        this.type = type;
        this.name = name;
        this.isPrimary = isPrimary;
        this.nthSibling = nthSibling;
    }
    
    /**
     * @see org.alfresco.module.phpIntegration.lib.ScriptObject#getScriptObjectName()
     */
    public String getScriptObjectName()
    {
        return SCRIPT_OBJECT_NAME;
    }
    
    /**
     * Gets the parent node
     * 
     * @return  Node    the parent node
     */
    public Node getParent()
    {
        return this.parent;
    }
    
    /**
     * Get the child node
     * 
     * @return  Node    the child node
     */
    public Node getChild()
    {
        return this.child;
    }
    
    /**
     * Get the type of the association
     * 
     * @return  Stirng  the type of the association
     */
    public String getType()
    {
        return this.type;
    }
    
    /**
     * Get the name of the association
     * 
     * @return  String  the name of the association
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Indicates whehter the association is primary
     * 
     * @return  boolean     true if assocaition is primary, false otherwise
     */
    public boolean getIsPrimary()
    {
        return this.isPrimary;
    }
    
    /**
     * The sibling order
     * 
     * @return  int     the sibling order
     */
    public int getNthSibling()
    {
        return this.nthSibling;
    }
}
