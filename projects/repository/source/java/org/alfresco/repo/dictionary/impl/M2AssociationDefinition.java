package org.alfresco.repo.dictionary.impl;

import org.alfresco.repo.dictionary.AssociationDefinition;
import org.alfresco.repo.dictionary.ClassDefinition;
import org.alfresco.repo.dictionary.DictionaryException;
import org.alfresco.repo.ref.NamespacePrefixResolver;
import org.alfresco.repo.ref.QName;


/**
 * Compiled Association Definition.
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
     * Construct
     * 
     * @param m2Association  association definition
     * @return  the definition
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


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getTitle()
     */
    public String getTitle()
    {
        return assoc.getTitle();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getDescription()
     */
    public String getDescription()
    {
        return assoc.getDescription();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isProtected()
     */
    public boolean isProtected()
    {
        return assoc.isProtected();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getSourceClass()
     */
    public ClassDefinition getSourceClass()
    {
        return classDef;
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getSourceRoleName()
     */
    public QName getSourceRoleName()
    {
        return sourceRoleName;
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isSourceMandatory()
     */
    public boolean isSourceMandatory()
    {
        return assoc.isSourceMandatory();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isSourceMany()
     */
    public boolean isSourceMany()
    {
        return assoc.isSourceMany();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getTargetClass()
     */
    public ClassDefinition getTargetClass()
    {
        return targetClass;
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#getTargetRoleName()
     */
    public QName getTargetRoleName()
    {
        return targetRoleName;
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isTargetMandatory()
     */
    public boolean isTargetMandatory()
    {
        return assoc.isTargetMandatory();
    }


    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.AssociationDefinition#isTargetMany()
     */
    public boolean isTargetMany()
    {
        return assoc.isTargetMany();
    }

}
