package org.alfresco.repo.dictionary.metamodel.emf;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import org.alfresco.repo.ref.QName;

/**
 * @model abstract="true"
 */
public interface EMFClass extends EObject
{

    /**
     * @model
     */
    public QName getEmfName();

    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfName <em>Emf Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Name</em>' attribute.
     * @see #getEmfName()
     * @generated
     */
    void setEmfName(QName value);

    /**
     * @model type="EMFClass"
     */
    public EMFClass getEmfSuperClass();
        
    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFClass#getEmfSuperClass <em>Emf Super Class</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Super Class</em>' reference.
     * @see #getEmfSuperClass()
     * @generated
     */
    void setEmfSuperClass(EMFClass value);

    /**
     * @model type="EMFProperty" opposite="emfContainerClass" containment="true"
     */
    EList getEmfProperties();

    /**
     * @model type="EMFAssociation" opposite="emfContainerClass" containment="true"
     */
    EList getEmfAssociations();
    
}
