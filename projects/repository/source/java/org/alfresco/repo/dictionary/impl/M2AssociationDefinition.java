package org.alfresco.repo.dictionary.impl;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryException;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;


/**
 * Default Read-Only Association Definition Implementation
 * 
 * @author David Caruana
 */
/*package*/ class M2AssociationDefinition implements AssociationDefinition
{

    private ClassDefinition classDef;
    private M2ClassAssociation assoc;
    private QName name;
    private QName targetClassName;
    private ClassDefinition targetClass;
    private QName sourceRoleName;
    private QName targetRoleName;
    
    
    /**
     * Construct read-only Association Definition
     * 
     * @param m2Association  association definition
     * @return  the read-only definition
     */
    
    /*package*/ M2AssociationDefinition(ClassDefinition classDef, M2ClassAssociation assoc, NamespacePrefixResolver resolver)
    {
        this.classDef = classDef;
        this.assoc = assoc;
        
        // Resolve names
        this.name = QName.createQName(assoc.getName(), resolver);
        this.targetClassName = QName.createQName(assoc.getTargetClassName(), resolver);
        this.sourceRoleName = QName.createQName(assoc.getSourceRoleName(), resolver);
        this.targetRoleName = QName.createQName(assoc.getTargetRoleName(), resolver);
    }

    /*package*/ M2ClassAssociation getM2Association()
    {
        return assoc;
    }


    /*package*/ void resolveDependencies(ModelQuery query)
    {
        if (targetClassName == null)
        {
            throw new DictionaryException("Target class of association " + name.toPrefixString() + " must be specified");
        }
        targetClass = query.getClass(targetClassName);
        if (targetClass == null)
        {
            throw new DictionaryException("Target class " + targetClassName.toPrefixString() + " of association " + name.toPrefixString() + " is not found");
        }
    }
    

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getName()
     */
    public QName getName()
    {
        return name;
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isChild()
     */
    public boolean isChild()
    {
        return (assoc instanceof M2ChildAssociation);
    }


    public String getTitle()
    {
        return assoc.getTitle();
    }


    public String getDescription()
    {
        return assoc.getDescription();
    }


    public boolean isProtected()
    {
        return assoc.isProtected();
    }


    public ClassDefinition getSourceClass()
    {
        return classDef;
    }


    public QName getSourceRoleName()
    {
        return sourceRoleName;
    }


    public boolean isSourceMandatory()
    {
        return assoc.isSourceMandatory();
    }


    public boolean isSourceMany()
    {
        return assoc.isSourceMany();
    }


    public ClassDefinition getTargetClass()
    {
        return targetClass;
    }


    public QName getTargetRoleName()
    {
        return targetRoleName;
    }


    public boolean isTargetMandatory()
    {
        return assoc.isTargetMandatory();
    }


    public boolean isTargetMany()
    {
        return assoc.isTargetMany();
    }


}
