package org.alfresco.repo.ref;

import java.io.Serializable;

import org.alfresco.util.EqualsHelper;

/**
 * This class represents a regular, named node relationship between two nodes.
 * 
 * @author Derek Hulley
 */
public class NodeAssocRef implements EntityRef, Serializable
{
    private static final long serialVersionUID = 3977867284482439475L;

    private NodeRef sourceRef;
    private QName assocQName;
    private NodeRef targetRef;

    /**
     * Construct a representation of a source --- name ----> target
     * relationship.
     * 
     * @param sourceRef
     *            the source reference - never null
     * @param assocQName
     *            the qualified name of the association - never null
     * @param targetRef
     *            the target node reference - never null.
     */
    public NodeAssocRef(NodeRef sourceRef, QName assocQName, NodeRef targetRef)
    {
        this.sourceRef = sourceRef;
        this.assocQName = assocQName;
        this.targetRef = targetRef;

        // check
        if (sourceRef == null)
        {
            throw new IllegalArgumentException("Source reference may not be null");
        }
        if (assocQName == null)
        {
            throw new IllegalArgumentException("QName may not be null");
        }
        if (targetRef == null)
        {
            throw new IllegalArgumentException("Target reference may not be null");
        }
    }

    /**
     * Get the qualified name of the source-target association
     * 
     * @return Returns the qualified name of the source-target association.
     */
    public QName getQName()
    {
        return assocQName;
    }

    /**
     * @return Returns the child node reference - never null
     */
    public NodeRef getTargetRef()
    {
        return targetRef;
    }

    /**
     * @return Returns the parent node reference, which may be null if this
     *         represents the imaginary reference to the root node
     */
    public NodeRef getSourceRef()
    {
        return sourceRef;
    }

    /**
     * Compares:
     * <ul>
     * <li>{@link #sourceRef}</li>
     * <li>{@link #targetRef}</li>
     * <li>{@link #assocQName}</li>
     * </ul>
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ChildAssocRef))
        {
            return false;
        }
        NodeAssocRef other = (NodeAssocRef) o;

        return (EqualsHelper.nullSafeEquals(this.sourceRef, other.sourceRef)
                && EqualsHelper.nullSafeEquals(this.assocQName, other.assocQName)
                && EqualsHelper.nullSafeEquals(this.targetRef, other.targetRef));
    }

    public int hashCode()
    {
        int hashCode = (getSourceRef() == null) ? 0 : getSourceRef().hashCode();
        hashCode = 37 * hashCode + ((getQName() == null) ? 0 : getQName().hashCode());
        hashCode = 37 * hashCode + getTargetRef().hashCode();
        return hashCode;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getSourceRef());
        buffer.append(" --- ").append(getQName()).append(" ---> ");
        buffer.append(getTargetRef());
        return buffer.toString();
    }
}
