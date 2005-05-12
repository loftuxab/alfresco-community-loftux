package org.alfresco.repo.dictionary.metamodel;

import java.util.List;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.AssociationRef;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.ref.QName;


/**
 * Default Read-Only Association Definition Implementation
 * 
 * @author David Caruana
 */
public class M2AssociationDefinition implements AssociationDefinition
{

    /**
     * Association definition to wrap
     */
    private M2Association m2Association;
    
    private AssociationRef assocRef;

    
    /**
     * Construct read-only Association Definition
     * 
     * @param m2Association  association definition
     * @return  the read-only definition
     */
    public static AssociationDefinition create(M2Association m2Association)
    {
        if (m2Association instanceof M2ChildAssociation)
        {
            return new M2ChildAssociationDefinition((M2ChildAssociation)m2Association);
        }
        else
        {
            return new M2AssociationDefinition(m2Association);
        }
    }
    
    
    /*package*/ M2AssociationDefinition(M2Association m2Association)
    {
        this.m2Association = m2Association;
        
        // Force load-on-demand of related entities
        this.m2Association.getContainerClass();
        this.m2Association.getRequiredToClasses();
    }

    
    /*package*/ M2Association getM2Association()
    {
        return m2Association;
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getName()
     */
    public QName getName()
    {
        return getReference().getQName();
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getReference()
     */
    public AssociationRef getReference()
    {
        if (assocRef == null)
        {
            assocRef = new AssociationRef(getContainerClass().getReference(),
                    m2Association.getName());
        }
        return assocRef;
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getContainerClass()
     */
    public ClassDefinition getContainerClass()
    {
        return m2Association.getContainerClass().getClassDefinition();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isChild()
     */
    public boolean isChild()
    {
        return (m2Association instanceof M2ChildAssociation);
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isMultiValued()
     */
    public boolean isMultiValued()
    {
        return m2Association.isMultiValued();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isMandatory()
     */
    public boolean isMandatory()
    {
        return m2Association.isMandatory();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isProtected()
     */
    public boolean isProtected()
    {
        return m2Association.isProtected();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getRequiredToClasses()
     */
    public List<ClassDefinition> getRequiredToClasses()
    {
        return M2References.createClassDefList(m2Association.getRequiredToClasses());
    }

}
