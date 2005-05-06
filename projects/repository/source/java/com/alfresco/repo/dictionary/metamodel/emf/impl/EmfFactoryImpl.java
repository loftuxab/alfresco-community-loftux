/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.impl;

import org.alfresco.repo.dictionary.metamodel.emf.*;

import org.alfresco.repo.ref.QName;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class EmfFactoryImpl extends EFactoryImpl implements EmfFactory
{
    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EmfFactoryImpl()
    {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject create(EClass eClass)
    {
        switch (eClass.getClassifierID())
        {
            case EmfPackage.EMF_PROPERTY: return createEMFProperty();
            case EmfPackage.EMF_ASPECT: return createEMFAspect();
            case EmfPackage.EMF_ASSOCIATION: return createEMFAssociation();
            case EmfPackage.EMF_CHILD_ASSOCIATION: return createEMFChildAssociation();
            case EmfPackage.EMF_PROPERTY_TYPE: return createEMFPropertyType();
            case EmfPackage.EMF_TYPE: return createEMFType();
            case EmfPackage.EMF_NAMESPACE_PREFIX: return createEMFNamespacePrefix();
            case EmfPackage.EMF_NAMESPACE_URI: return createEMFNamespaceURI();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object createFromString(EDataType eDataType, String initialValue)
    {
        switch (eDataType.getClassifierID())
        {
            case EmfPackage.QNAME:
                return createQNameFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertToString(EDataType eDataType, Object instanceValue)
    {
        switch (eDataType.getClassifierID())
        {
            case EmfPackage.QNAME:
                return convertQNameToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFProperty createEMFProperty()
    {
        EMFPropertyImpl emfProperty = new EMFPropertyImpl();
        return emfProperty;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFAspect createEMFAspect()
    {
        EMFAspectImpl emfAspect = new EMFAspectImpl();
        return emfAspect;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFAssociation createEMFAssociation()
    {
        EMFAssociationImpl emfAssociation = new EMFAssociationImpl();
        return emfAssociation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFChildAssociation createEMFChildAssociation()
    {
        EMFChildAssociationImpl emfChildAssociation = new EMFChildAssociationImpl();
        return emfChildAssociation;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFPropertyType createEMFPropertyType()
    {
        EMFPropertyTypeImpl emfPropertyType = new EMFPropertyTypeImpl();
        return emfPropertyType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFType createEMFType()
    {
        EMFTypeImpl emfType = new EMFTypeImpl();
        return emfType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFNamespacePrefix createEMFNamespacePrefix()
    {
        EMFNamespacePrefixImpl emfNamespacePrefix = new EMFNamespacePrefixImpl();
        return emfNamespacePrefix;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EMFNamespaceURI createEMFNamespaceURI()
    {
        EMFNamespaceURIImpl emfNamespaceURI = new EMFNamespaceURIImpl();
        return emfNamespaceURI;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     */
    public QName createQNameFromString(EDataType eDataType, String initialValue)
    {
        return QName.createQName(initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertQNameToString(EDataType eDataType, Object instanceValue)
    {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EmfPackage getEmfPackage()
    {
        return (EmfPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    public static EmfPackage getPackage()
    {
        return EmfPackage.eINSTANCE;
    }

} //EmfFactoryImpl
