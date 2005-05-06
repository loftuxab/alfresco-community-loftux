package org.alfresco.repo.dictionary.metamodel.emf;

import org.alfresco.repo.ref.QName;

import org.eclipse.emf.ecore.EObject;

/**
 * @model
 */
public interface EMFPropertyType extends EObject{

    /**
     * Returns the value of the '<em><b>QName</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>QName</em>' attribute.
     * @see #setQName(QName)
     * @see org.alfresco.repo.dictionary.metamodel.emf.EmfPackage#getEMFPropertyType_QName()
     * @model dataType="org.alfresco.repo.dictionary.metamodel.emf.QName"
     * @generated
     */
    QName getQName();

    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType#getQName <em>QName</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>QName</em>' attribute.
     * @see #getQName()
     * @generated
     */
    void setQName(QName value);

    /**
     * Returns the value of the '<em><b>Emf Analyser Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Emf Analyser Class Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Emf Analyser Class Name</em>' attribute.
     * @see #setEmfAnalyserClassName(String)
     * @see org.alfresco.repo.dictionary.metamodel.emf.EmfPackage#getEMFPropertyType_EmfAnalyserClassName()
     * @model
     * @generated
     */
    String getEmfAnalyserClassName();

    /**
     * Sets the value of the '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType#getEmfAnalyserClassName <em>Emf Analyser Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Emf Analyser Class Name</em>' attribute.
     * @see #getEmfAnalyserClassName()
     * @generated
     */
    void setEmfAnalyserClassName(String value);

}
