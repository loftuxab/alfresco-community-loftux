package com.activiti.repo.dictionary.metamodel.emf;

import com.activiti.repo.ref.QName;

import org.eclipse.emf.ecore.EObject;

/**
 * @model
 */
public interface EMFPropertyType extends EObject {

    /**
     * @model
     */
    QName getName();
    
    
    /**
     * Sets the value of the '{@link com.activiti.repo.dictionary.metamodel.emf.EMFPropertyType#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(QName value);

}
