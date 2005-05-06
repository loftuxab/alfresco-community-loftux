package org.alfresco.repo.dictionary.metamodel.emf;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.alfresco.repo.dictionary.metamodel.emf.EmfPackage
 * @generated
 */
public interface EmfFactory extends EFactory{
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EmfFactory eINSTANCE = new org.alfresco.repo.dictionary.metamodel.emf.impl.EmfFactoryImpl();

    /**
     * Returns a new object of class '<em>EMF Property</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>EMF Property</em>'.
     * @generated
     */
    EMFProperty createEMFProperty();

    /**
     * Returns a new object of class '<em>EMF Aspect</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>EMF Aspect</em>'.
     * @generated
     */
    EMFAspect createEMFAspect();

    /**
     * Returns a new object of class '<em>EMF Association</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>EMF Association</em>'.
     * @generated
     */
    EMFAssociation createEMFAssociation();

    /**
     * Returns a new object of class '<em>EMF Child Association</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>EMF Child Association</em>'.
     * @generated
     */
    EMFChildAssociation createEMFChildAssociation();

    /**
     * Returns a new object of class '<em>EMF Property Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>EMF Property Type</em>'.
     * @generated
     */
    EMFPropertyType createEMFPropertyType();

    /**
     * Returns a new object of class '<em>EMF Type</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>EMF Type</em>'.
     * @generated
     */
    EMFType createEMFType();

    /**
     * Returns a new object of class '<em>EMF Namespace Prefix</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>EMF Namespace Prefix</em>'.
     * @generated
     */
    EMFNamespacePrefix createEMFNamespacePrefix();

    /**
     * Returns a new object of class '<em>EMF Namespace URI</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>EMF Namespace URI</em>'.
     * @generated
     */
    EMFNamespaceURI createEMFNamespaceURI();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    EmfPackage getEmfPackage();

} //EmfFactory
