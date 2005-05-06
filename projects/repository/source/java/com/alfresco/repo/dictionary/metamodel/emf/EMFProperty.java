package org.alfresco.repo.dictionary.metamodel.emf;


import org.eclipse.emf.ecore.EObject;

/**
 * @model
 */
public interface EMFProperty extends EObject{

    /**
     * @model
     */
    String getEmfName();
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfName <em>Emf Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Name</em>' attribute.
     * @see #getEmfName()
     * @generated
     */
    void setEmfName(String value);

    /**
     * @model default="false"
     */
    Boolean getEmfProtected();
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfProtected <em>Emf Protected</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Protected</em>' attribute.
     * @see #getEmfProtected()
     * @generated
     */
    void setEmfProtected(Boolean value);

    /**
     * @model default="false"
     */
    Boolean getEmfMandatory();

    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfMandatory <em>Emf Mandatory</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Mandatory</em>' attribute.
     * @see #getEmfMandatory()
     * @generated
     */
    void setEmfMandatory(Boolean value);

    /**
     * @model default="false"
     */
    Boolean getEmfMultiple();
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfMultiple <em>Emf Multiple</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Multiple</em>' attribute.
     * @see #getEmfMultiple()
     * @generated
     */
    void setEmfMultiple(Boolean value);

    /**
     * @model default="true"
     */
    Boolean getEmfIndexed();

    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfIndexed <em>Emf Indexed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Indexed</em>' attribute.
     * @see #getEmfIndexed()
     * @generated
     */
    void setEmfIndexed(Boolean value);

    /**
     * @model default="false"
     */
    Boolean getEmfStoredInIndex();
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfStoredInIndex <em>Emf Stored In Index</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Stored In Index</em>' attribute.
     * @see #getEmfStoredInIndex()
     * @generated
     */
    void setEmfStoredInIndex(Boolean value);

    /**
     * Returns the value of the '<em><b>Emf Tokenised In Index</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Emf Tokenised In Index</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Emf Tokenised In Index</em>' attribute.
     * @see #setEmfTokenisedInIndex(Boolean)
     * @see org.alfresco.repo.dictionary.metamodel.emf.EmfPackage#getEMFProperty_EmfTokenisedInIndex()
     * @model default="false"
     * @generated
     */
    Boolean getEmfTokenisedInIndex();

    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfTokenisedInIndex <em>Emf Tokenised In Index</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Tokenised In Index</em>' attribute.
     * @see #getEmfTokenisedInIndex()
     * @generated
     */
    void setEmfTokenisedInIndex(Boolean value);

    /**
     * Returns the value of the '<em><b>Emf Index Atomically</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Emf Index Atomically</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Emf Index Atomically</em>' attribute.
     * @see #setEmfIndexAtomically(Boolean)
     * @see org.alfresco.repo.dictionary.metamodel.emf.EmfPackage#getEMFProperty_EmfIndexAtomically()
     * @model default="true"
     * @generated
     */
    Boolean getEmfIndexAtomically();

    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfIndexAtomically <em>Emf Index Atomically</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Index Atomically</em>' attribute.
     * @see #getEmfIndexAtomically()
     * @generated
     */
    void setEmfIndexAtomically(Boolean value);

    /**
     * @model type="EMFPropertyType"
     */
    EMFPropertyType getEmfType();
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty#getEmfType <em>Emf Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Type</em>' reference.
     * @see #getEmfType()
     * @generated
     */
    void setEmfType(EMFPropertyType value);

    /**
     * @model type="EMFClass" opposite="emfProperties" changeable="false"
     */
    EMFClass getEmfContainerClass();
    
}
