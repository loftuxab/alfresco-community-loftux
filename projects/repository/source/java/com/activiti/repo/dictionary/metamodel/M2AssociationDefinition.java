package com.activiti.repo.dictionary.metamodel;

import java.util.List;

import com.activiti.repo.dictionary.AssociationDefinition;
import com.activiti.repo.dictionary.AssociationRef;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.ref.QName;


public class M2AssociationDefinition implements AssociationDefinition
{

    private M2Association m2Association;

    
    public static M2AssociationDefinition create(M2Association m2Association)
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
    

    public QName getName()
    {
        return getReference().getQName();
    }

    
    public AssociationRef getReference()
    {
        return m2Association.getReference();
    }


    public ClassRef getContainerClass()
    {
        return m2Association.getContainerClass().getReference();
    }


    public boolean isChild()
    {
        return (m2Association instanceof M2ChildAssociation);
    }


    public boolean isMultiValued()
    {
        return m2Association.isMultiValued();
    }


    public boolean isMandatory()
    {
        return m2Association.isMandatory();
    }


    public boolean isProtected()
    {
        return m2Association.isProtected();
    }


    public List getRequiredToClasses()
    {
        return M2References.createClassRefList(m2Association.getRequiredToClasses());
    }

}
