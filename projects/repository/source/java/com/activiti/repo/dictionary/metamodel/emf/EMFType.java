package com.activiti.repo.dictionary.metamodel.emf;

import org.eclipse.emf.common.util.EList;

/**
 * @model
 */
public interface EMFType extends EMFClass{

    /**
     * @model
     */
    Boolean getEmfOrderedChildren();
    
    /**
     * Sets the value of the '{@link com.activiti.repo.dictionary.metamodel.emf.EMFType#getEmfOrderedChildren <em>Emf Ordered Children</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Ordered Children</em>' attribute.
     * @see #getEmfOrderedChildren()
     * @generated
     */
    void setEmfOrderedChildren(Boolean value);

    /**
     * @model type="EMFAspect"
     */
    EList getEmfDefaultAspects();
    
}
