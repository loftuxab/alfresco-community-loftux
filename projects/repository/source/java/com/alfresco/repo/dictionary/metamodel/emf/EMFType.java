package org.alfresco.repo.dictionary.metamodel.emf;

import org.eclipse.emf.common.util.EList;

/**
 * @model
 */
public interface EMFType extends EMFClass{

    /**
     * @model default="false"
     */
    Boolean getEmfOrderedChildren();
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFType#getEmfOrderedChildren <em>Emf Ordered Children</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Ordered Children</em>' attribute.
     * @see #getEmfOrderedChildren()
     * @generated
     */
    void setEmfOrderedChildren(Boolean value);

    /**
     * @model default="true"
     */
    Boolean getEmfStrict();
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFType#getEmfStrict <em>Emf Strict</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Strict</em>' attribute.
     * @see #getEmfStrict()
     * @generated
     */
    void setEmfStrict(Boolean value);

    /**
     * @model type="EMFAspect"
     */
    EList getEmfDefaultAspects();
    
}
