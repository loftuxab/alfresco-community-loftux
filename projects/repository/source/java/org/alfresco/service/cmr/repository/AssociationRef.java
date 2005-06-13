package org.alfresco.service.cmr.repository;

import java.io.Serializable;

import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;

/**
 * This class represents a regular, named node relationship between two nodes.
 * 
 * @author Derek Hulley
 */
public class AssociationRef implements EntityRef, Serializable
{
    private static final long serialVersionUID = 3977867284482439475L;

    private NodeRef sourceRef;
    private QName assocTypeQName;
    private NodeRef targetRef;

    /**
     * Construct a representation of a source --- name ----> target
     * relationship.
     * 
     * @param sourceRef
     *            the source reference - never null
     * @param assocTypeQName
     *            the qualified name of the association type - never null
     * @param targetRef
     *            the target node reference - never null.
     */
    public AssociationRef(NodeRef sourceRef, QName assocTypeQName, NodeRef targetRef)
    {
        this.sourceRef = sourceRef;
        this.assocTypeQName = assocTypeQName;
        this.targetRef = targetRef;

        // check
        if (sourceRef == null)
        {
            throw new IllegalArgumentException("Source reference may not be null");
        }
        if (assocTypeQName == null)
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
    public QName getTypeQName()
    {
        return assocTypeQName;
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
     * <li>{@link #assocTypeQName}</li>
     * </ul>
     */
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof ChildAssociationRef))
        {
            return false;
        }
        AssociationRef other = (AssociationRef) o;

        return (EqualsHelper.nullSafeEquals(this.sourceRef, other.sourceRef)
                && EqualsHelper.nullSafeEquals(this.assocTypeQName, other.assocTypeQName)
                && EqualsHelper.nullSafeEquals(this.targetRef, other.targetRef));
    }

    public int hashCode()
    {
        int hashCode = (getSourceRef() == null) ? 0 : getSourceRef().hashCode();
        hashCode = 37 * hashCode + ((getTypeQName() == null) ? 0 : getTypeQName().hashCode());
        hashCode = 37 * hashCode + getTargetRef().hashCode();
        return hashCode;
    }

    public String toString()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getSourceRef());
        buffer.append(" --- ").append(getTypeQName()).append(" ---> ");
        buffer.append(getTargetRef());
        return buffer.toString();
    }
}
