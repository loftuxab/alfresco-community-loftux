/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.alfresco.repo.dictionary.metamodel.emf.util;

import org.alfresco.repo.dictionary.metamodel.emf.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.alfresco.repo.dictionary.metamodel.emf.EmfPackage
 * @generated
 */
public class EmfAdapterFactory extends AdapterFactoryImpl
{
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static EmfPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EmfAdapterFactory()
    {
        if (modelPackage == null)
        {
            modelPackage = EmfPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    public boolean isFactoryForType(Object object)
    {
        if (object == modelPackage)
        {
            return true;
        }
        if (object instanceof EObject)
        {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch the delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EmfSwitch modelSwitch =
        new EmfSwitch()
        {
            public Object caseEMFClass(EMFClass object)
            {
                return createEMFClassAdapter();
            }
            public Object caseEMFProperty(EMFProperty object)
            {
                return createEMFPropertyAdapter();
            }
            public Object caseEMFAspect(EMFAspect object)
            {
                return createEMFAspectAdapter();
            }
            public Object caseEMFAssociation(EMFAssociation object)
            {
                return createEMFAssociationAdapter();
            }
            public Object caseEMFChildAssociation(EMFChildAssociation object)
            {
                return createEMFChildAssociationAdapter();
            }
            public Object caseEMFPropertyType(EMFPropertyType object)
            {
                return createEMFPropertyTypeAdapter();
            }
            public Object caseEMFType(EMFType object)
            {
                return createEMFTypeAdapter();
            }
            public Object caseEMFNamespacePrefix(EMFNamespacePrefix object)
            {
                return createEMFNamespacePrefixAdapter();
            }
            public Object caseEMFNamespaceURI(EMFNamespaceURI object)
            {
                return createEMFNamespaceURIAdapter();
            }
            public Object defaultCase(EObject object)
            {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    public Adapter createAdapter(Notifier target)
    {
        return (Adapter)modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFClass <em>EMF Class</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFClass
     * @generated
     */
    public Adapter createEMFClassAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFProperty <em>EMF Property</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFProperty
     * @generated
     */
    public Adapter createEMFPropertyAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAspect <em>EMF Aspect</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAspect
     * @generated
     */
    public Adapter createEMFAspectAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation <em>EMF Association</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFAssociation
     * @generated
     */
    public Adapter createEMFAssociationAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFChildAssociation <em>EMF Child Association</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFChildAssociation
     * @generated
     */
    public Adapter createEMFChildAssociationAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType <em>EMF Property Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFPropertyType
     * @generated
     */
    public Adapter createEMFPropertyTypeAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFType <em>EMF Type</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFType
     * @generated
     */
    public Adapter createEMFTypeAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix <em>EMF Namespace Prefix</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFNamespacePrefix
     * @generated
     */
    public Adapter createEMFNamespacePrefixAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI <em>EMF Namespace URI</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.alfresco.repo.dictionary.metamodel.emf.EMFNamespaceURI
     * @generated
     */
    public Adapter createEMFNamespaceURIAdapter()
    {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter()
    {
        return null;
    }

} //EmfAdapterFactory
