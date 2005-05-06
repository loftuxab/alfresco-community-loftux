package org.alfresco.repo.dictionary.metamodel.emf;

import org.eclipse.emf.ecore.EObject;

/**
 * @model
 */
public interface EMFNamespacePrefix extends EObject{

    /**
     * @model
     */
    String getEmfPrefix();
    
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix#getEmfPrefix <em>Emf Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Prefix</em>' attribute.
     * @see #getEmfPrefix()
     * @generated
     */
    void setEmfPrefix(String value);

    /**
     * @model type="EMFNamespaceURI" opposite="emfPrefixes"
     */
    EMFNamespaceURI getEmfURI();

    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix#getEmfURI <em>Emf URI</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf URI</em>' reference.
     * @see #getEmfURI()
     * @generated
     */
    void setEmfURI(EMFNamespaceURI value);

}
