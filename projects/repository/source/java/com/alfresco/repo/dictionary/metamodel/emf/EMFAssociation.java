package org.alfresco.repo.dictionary.metamodel.emf;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @model
 */
public interface EMFAssociation extends EObject
{

    /**
     * @model
     */
    String getEmfName();
    
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfName <em>Emf Name</em>}' attribute.
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
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfProtected <em>Emf Protected</em>}' attribute.
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
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfMandatory <em>Emf Mandatory</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Mandatory</em>' attribute.
     * @see #getEmfMandatory()
     * @generated
     */
    void setEmfMandatory(Boolean value);

    /**
     * @model default="true"
     */
    Boolean getEmfMultiple();

    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation#getEmfMultiple <em>Emf Multiple</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Multiple</em>' attribute.
     * @see #getEmfMultiple()
     * @generated
     */
    void setEmfMultiple(Boolean value);

    /**
     * @model type="EMFClass" opposite="emfAssociations" changeable="false"
     */
    EMFClass getEmfContainerClass();
    
    /**
     * @model type="EMFClass"
     */
    EList getEmfRequiredToClasses();
        

}
