package org.alfresco.repo.dictionary.metamodel.emf;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @model
 */
public interface EMFNamespaceURI extends EObject{

    /**
     * @model
     */
    String getEmfURI();

    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI#getEmfURI <em>Emf URI</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf URI</em>' attribute.
     * @see #getEmfURI()
     * @generated
     */
    void setEmfURI(String value);

    /**
     * @model type="EMFNamespacePrefix" opposite="emfURI"
     */
    EList getEmfPrefixes();

}
