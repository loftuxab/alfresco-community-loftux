package org.alfresco.repo.dictionary.metamodel.emf;


/**
 * @model
 */
public interface EMFChildAssociation extends EMFAssociation
{

    /**
     * @model type="EMFType"
     */
    EMFType getEmfDefaultType();
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFChildAssociation#getEmfDefaultType <em>Emf Default Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Default Type</em>' reference.
     * @see #getEmfDefaultType()
     * @generated
     */
    void setEmfDefaultType(EMFType value);

}
